import Foundation
import Capacitor
import FirebaseMLVision

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(FirebaseMLVision)
public class FirebaseMLVision: CAPPlugin {
    lazy var vision = Vision.vision()

    @objc func detectInImage(_ call: CAPPluginCall) {
        let image: VisionImage
        let options: VisionFaceDetectorOptions = VisionFaceDetectorOptions()

        if let content = call.getString("image") {
            image = VisionImage.init(
                image: UIImage(
                    data: Data(base64Encoded: content)!
                )!
            )
        } else {
            call.reject("Must provide an image")
            return
        }

        if let optionsObject = call.getObject("options") {
            if let landmarkMode = optionsObject["landmarkMode"] {
                options.landmarkMode = landmarkMode as! VisionFaceDetectorLandmarkMode
            }
            if let contourMode = optionsObject["contourMode"] {
                options.contourMode = contourMode as! VisionFaceDetectorContourMode
            }
            if let classificationMode = optionsObject["classificationMode"] {
                options.classificationMode = classificationMode as! VisionFaceDetectorClassificationMode
            }
            if let performanceMode = optionsObject["performanceMode"] {
                options.performanceMode = performanceMode as! VisionFaceDetectorPerformanceMode
            }

            if let minFaceSize = optionsObject["minFaceSize"] {
                options.minFaceSize = minFaceSize as! CGFloat
            }

            if let enableTracking = optionsObject["enableTracking"] {
                options.isTrackingEnabled = enableTracking as! Bool
            }
        }

        let faceDetector = vision.faceDetector(options: options)

        let faces: [VisionFace]
        do {
            faces = try faceDetector.results(in: image)
        } catch let error {
            call.error(error.localizedDescription, error)
            return
        }

        var facesArray = [Any]()

        for face in faces {
            var faceObject = [String: Any]()

            do { // Bounds
                let bounds = face.frame

                var boundsObject = [String: Any]()

                boundsObject["x"] = bounds.origin.x
                boundsObject["y"] = bounds.origin.y
                boundsObject["width"] = bounds.size.width
                boundsObject["height"] = bounds.size.height

                boundsObject["left"] = bounds.origin.x
                boundsObject["top"] = bounds.origin.y
                boundsObject["right"] = bounds.origin.x + bounds.size.width
                boundsObject["bottom"] = bounds.origin.y + bounds.size.height

                faceObject["bounds"] = boundsObject
            }

            do { // Landmarks
                var landmarksArray = [Any]()

                let landmarkTypes: [FaceLandmarkType] = [
                    // The midpoint between the subject's left mouth corner and the outer corner of the subject's left eye.
                    .leftCheek,
                    // The midpoint between the subject's right mouth corner and the outer corner of the subject's right eye.
                    .rightCheek,

                    // The midpoint of the subject's left ear tip and left ear lobe.
                    .leftEar,
                    // The midpoint of the subject's right ear tip and right ear lobe.
                    .rightEar,

                    // The center of the subject's left eye cavity.
                    .leftEye,
                    // The midpoint of the subject's right ear tip and right ear lobe.
                    .rightEye,

                    // The midpoint between the subject's nostrils where the nose meets the face.
                    .noseBase,

                    // The center of the subject's bottom lip.
                    .mouthBottom,
                    // The subject's left mouth corner where the lips meet.
                    .mouthLeft,
                    // The subject's right mouth corner where the lips meet.
                    .mouthRight,
                ]

                for landmarkType in landmarkTypes {
                    if let landmark = landmarkHelper(face: face, landmarkType: landmarkType) {
                        landmarksArray.append(landmark)
                    }
                }

                if landmarksArray.count > 0 {
                    faceObject["landmarks"] = landmarksArray
                }
            }

            do { // Contours
                var contoursArray = [Any]()

                let contourTypes: [FaceContourType] = [
                    // All points of a face contour.
                    .all,

                    // The outline of the subject's face.
                    .face,

                    // The top outline of the subject's left eyebrow.
                    .leftEyebrowTop,
                    // The bottom outline of the subject's left eyebrow.
                    .leftEyebrowBottom,
                    // The top outline of the subject's right eyebrow.
                    .rightEyebrowTop,
                    // The bottom outline of the subject's right eyebrow.
                    .rightEyebrowBottom,

                    // The outline of the subject's left eye cavity.
                    .leftEye,
                    // The outline of the subject's right eye cavity.
                    .rightEye,

                    // The top outline of the subject's upper lip.
                    .upperLipTop,
                    // The bottom outline of the subject's upper lip.
                    .upperLipBottom,
                    // The top outline of the subject's lower lip.
                    .lowerLipTop,
                    // The bottom outline of the subject's lower lip.
                    .lowerLipBottom,

                    // The outline of the subject's nose bridge.
                    .noseBridge,
                    // The outline of the subject's nose bridge.
                    .noseBottom,
                ]

                for contourType in contourTypes {
                    if let contour = contourHelper(face: face, contourType: contourType) {
                        contoursArray.append(contour)
                    }
                }

                if contoursArray.count > 0 {
                    faceObject["contours"] = contoursArray
                }
            }

            if face.hasHeadEulerAngleY {
                // Returns the rotation of the face about the vertical axis of the image.
                faceObject["headEulerAngleY"] = face.headEulerAngleY
            }
            if face.hasHeadEulerAngleZ {
                // Returns the rotation of the face about the axis pointing out of the image.
                faceObject["headEulerAngleZ"] = face.headEulerAngleZ
            }

            if face.hasLeftEyeOpenProbability {
                // Returns a value between 0.0 and 1.0 giving a probability that the face's left eye is open.
                faceObject["leftEyeOpenProbability"] = face.leftEyeOpenProbability
            }
            if face.hasRightEyeOpenProbability {
                // Returns a value between 0.0 and 1.0 giving a probability that the face's right eye is open.
                faceObject["rightEyeOpenProbability"] = face.rightEyeOpenProbability
            }
            if face.hasSmilingProbability {
                // Returns a value between 0.0 and 1.0 giving a probability that the face is smiling.
                faceObject["smilingProbability"] = face.smilingProbability
            }

            if face.hasTrackingID {
                // Returns the tracking ID if the tracking is enabled.
                faceObject["trackingId"] = face.trackingID
            }

            facesArray.append(faceObject)
        }

        call.success([
            "faces": facesArray
        ])
    }

    private func landmarkHelper(
        face: VisionFace,
        landmarkType: FaceLandmarkType
    ) -> [String: Any]? {
        if let landmark: VisionFaceLandmark = face.landmark(ofType: landmarkType) {
            let point: VisionPoint = landmark.position

            var landmarkObject = [String: Any]()

            landmarkObject["type"] = landmark.type
            landmarkObject["position"] = pointHelper(point: point)

            return landmarkObject
        } else {
            return nil
        }
    }

    private func contourHelper(
        face: VisionFace,
        contourType: FaceContourType
    ) -> [String: Any]? {
        if let contour: VisionFaceContour = face.contour(ofType: contourType) {
            let points: [VisionPoint] = contour.points

            var pointsArray = [Any]()

            for point in points {
                pointsArray.append(pointHelper(point: point))
            }

            var contourObject = [String: Any]()

            contourObject["type"] = contour.type
            contourObject["points"] = pointsArray

            return contourObject
        } else {
            return nil
        }
    }

    private func pointHelper(
        point: VisionPoint
    ) -> [String: Any] {
        var pointObject = [String: Any]()

        pointObject["x"] = point.x
        pointObject["y"] = point.y
        pointObject["z"] = point.z

        return pointObject
    }
}
