package com.ionicframework.capacitor;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Base64;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.ContourType;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark.LandmarkType;

import java.util.Arrays;
import java.util.List;

@NativePlugin(
        // Some Plugins will require you to request permissions.
        // First declare your plugin permissions.
        permissions = {
        }
)
public class FirebaseMLVision extends Plugin {
    private interface PointHelper {
        JSObject get(FirebaseVisionPoint point);
    }

    PointHelper pointHelper = (point) -> {
        JSObject pointObject = new JSObject();

        // Gets x coordinate.
        pointObject.put("x", point.getX());
        // Gets y coordinate.
        pointObject.put("y", point.getY());
        // Gets z coordinate (or depth).
        pointObject.put("z", point.getZ());

        return pointObject;
    };

    private interface LandmarkHelper {
        JSObject get(FirebaseVisionFace face, @LandmarkType int landmarkType);
    }

    LandmarkHelper landmarkHelper = (face, landmarkType) -> {
        // Represent a face landmark. A landmark is a point on a detected face, such as an eye, nose, or mouth.
        // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceLandmark
        FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkType);

        JSObject landmarkObject = new JSObject();

        if (landmark != null) {
            // Represent a 2D or 3D point for FirebaseVision.
            // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/common/FirebaseVisionPoint
            FirebaseVisionPoint point = landmark.getPosition();

            JSObject pointObject = pointHelper.get(point);

            // Gets the FirebaseVisionFaceLandmark.LandmarkType type.
            landmarkObject.put("type", landmark.getLandmarkType());
            // Gets a 2D point for landmark position, where (0, 0) is the upper-left corner of the image.
            landmarkObject.put("position", pointObject);
        }

        return landmarkObject;
    };

    private interface ContourHelper {
        JSObject get(FirebaseVisionFace face, @ContourType int contourType);
    }

    ContourHelper contourHelper = (face, contourType) -> {
        // Represent a face contour. A contour is a list of points on a detected face, such as the mouth.
        // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceContour
        FirebaseVisionFaceContour contour = face.getContour(contourType);

        JSObject contourObject = new JSObject();

        if (contour != null) {
            // Represent a 2D or 3D point for FirebaseVision.
            // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/common/FirebaseVisionPoint
            List<FirebaseVisionPoint> points = contour.getPoints();

            if (!points.isEmpty()) {
                JSArray pointsArray = new JSArray();

                for (FirebaseVisionPoint point : points) {
                    JSObject pointObject = pointHelper.get(point);

                    pointsArray.put(pointObject);
                }

                // Gets the FirebaseVisionFaceContour.ContourType type.
                contourObject.put("type", contour.getFaceContourType());
                // Gets a list of 2D points for this face contour, where (0, 0) is the upper-left corner of the image.
                contourObject.put("points", pointsArray);
            }
        }

        return contourObject;
    };

    @PluginMethod()
    public void detectInImage(final PluginCall call) {
        try {
            //Log.i(getLogTag(), "detectInImage");

            FirebaseVisionImage image = null;
            FirebaseVisionFaceDetectorOptions options = null;

            String content = call.getString("image", null);
            if (content != null) {
                final byte[] data = Base64.decode(content, Base64.DEFAULT);

                // Creates a FirebaseVisionImage from a Bitmap, where the object in the image should be already up-right and no rotation is needed.
                // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/common/FirebaseVisionImage
                image = FirebaseVisionImage.fromBitmap(
                        // Decode an immutable bitmap from the specified byte array.
                        BitmapFactory.decodeByteArray(data, 0, data.length)
                );
            }

            // Builder class of FirebaseVisionFaceDetectorOptions.
            // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.Builder
            FirebaseVisionFaceDetectorOptions.Builder builder = new FirebaseVisionFaceDetectorOptions.Builder();

            JSObject optionsObject = call.getObject("options", null);
            if (optionsObject != null) {
                Integer performanceMode = optionsObject.getInteger("performanceMode");
                Integer landmarkMode = optionsObject.getInteger("landmarkMode");
                Integer classificationMode = optionsObject.getInteger("classificationMode");
                Integer contourMode = optionsObject.getInteger("contourMode");

                Boolean enableTracking = optionsObject.getBoolean("enableTracking", false);

                if (performanceMode != null) {
                    // Extended option for controlling additional accuracy / speed trade-offs in performing face detection.
                    builder.setPerformanceMode(performanceMode);
                }
                if (landmarkMode != null) {
                    // Sets whether to detect no landmarks or all landmarks.
                    builder.setLandmarkMode(landmarkMode);
                }
                if (classificationMode != null) {
                    // Indicates whether to run additional classifiers for characterizing attributes such as "smiling" and "eyes open".
                    builder.setClassificationMode(classificationMode);
                }
                if (contourMode != null) {
                    // Sets whether to detect no contours or all contours.
                    builder.setContourMode(contourMode);
                }

                if (optionsObject.has("minFaceSize")) {
                    Double minFaceSize = optionsObject.getDouble("minFaceSize");

                    // Sets the smallest desired face size, expressed as a proportion of the width of the head to the image width.
                    builder.setMinFaceSize(minFaceSize.floatValue());
                }

                if (enableTracking == true) {
                    // Enables face tracking, which will maintain a consistent ID for each face when processing consecutive frames.
                    builder.enableTracking();
                }
            }

            // Builds a face detector instance.
            options = builder
                    .build();

            // Gets a FirebaseVisionFaceDetector that detects faces in a supplied image.
            // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetector
            FirebaseVisionFaceDetector faceDetector =
                    FirebaseVision.getInstance().getVisionFaceDetector(
                            // The options for the face detector.
                            options
                    );

            // Detects human faces from the supplied image.
            faceDetector
                    .detectInImage(
                            image
                    )
                    .addOnSuccessListener(
                            // A Task that asynchronously returns a List of detected FirebaseVisionFaces.
                            (List<FirebaseVisionFace> faces) -> {
                                //Log.i(getLogTag(), "addOnSuccessListener " + faces);

                                JSArray facesArray = new JSArray();

                                // Represents a face detected by FirebaseVisionFaceDetector.
                                // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFace
                                for (FirebaseVisionFace face : faces) {
                                    JSObject faceObject = new JSObject();

                                    {
                                        // Returns the axis-aligned bounding rectangle of the detected face.
                                        Rect bounds = face.getBoundingBox();

                                        JSObject boundsObject = new JSObject();

                                        // The X coordinate of the left side of the rectangle
                                        boundsObject.put("left", bounds.left);
                                        boundsObject.put("x", bounds.left);
                                        // The Y coordinate of the top of the rectangle
                                        boundsObject.put("top", bounds.top);
                                        boundsObject.put("y", bounds.top);
                                        // The X coordinate of the right side of the rectangle
                                        boundsObject.put("right", bounds.right);
                                        // The Y coordinate of the bottom of the rectangle
                                        boundsObject.put("bottom", bounds.bottom);
                                        // The rectangle's width.
                                        boundsObject.put("width", bounds.width());
                                        // The rectangle's height.
                                        boundsObject.put("height", bounds.height());

                                        faceObject.put("bounds", boundsObject);
                                    }

                                    {
                                        JSArray landmarksArray = new JSArray();

                                        List<Integer> landmarkTypes = Arrays.asList(
                                                // The midpoint between the subject's left mouth corner and the outer corner of the subject's left eye.
                                                FirebaseVisionFaceLandmark.LEFT_CHEEK,
                                                // The midpoint between the subject's right mouth corner and the outer corner of the subject's right eye.
                                                FirebaseVisionFaceLandmark.RIGHT_CHEEK,

                                                // The midpoint of the subject's left ear tip and left ear lobe.
                                                FirebaseVisionFaceLandmark.LEFT_EAR,
                                                // The midpoint of the subject's right ear tip and right ear lobe.
                                                FirebaseVisionFaceLandmark.RIGHT_EAR,

                                                // The center of the subject's left eye cavity.
                                                FirebaseVisionFaceLandmark.LEFT_EYE,
                                                // The midpoint of the subject's right ear tip and right ear lobe.
                                                FirebaseVisionFaceLandmark.RIGHT_EYE,

                                                // The midpoint between the subject's nostrils where the nose meets the face.
                                                FirebaseVisionFaceLandmark.NOSE_BASE,

                                                // The center of the subject's bottom lip.
                                                FirebaseVisionFaceLandmark.MOUTH_BOTTOM,
                                                // The subject's left mouth corner where the lips meet.
                                                FirebaseVisionFaceLandmark.MOUTH_LEFT,
                                                // The subject's right mouth corner where the lips meet.
                                                FirebaseVisionFaceLandmark.MOUTH_RIGHT
                                        );

                                        for (@LandmarkType int landmarkType : landmarkTypes) {
                                            JSObject landmarkObject = landmarkHelper.get(face, landmarkType);

                                            if (landmarkObject.length() > 0) {
                                                landmarksArray.put(landmarkObject);
                                            }
                                        }

                                        if (landmarksArray.length() > 0) {
                                            faceObject.put("landmarks", landmarksArray);
                                        }
                                    }

                                    {
                                        JSArray contoursArray = new JSArray();

                                        List<Integer> contourTypes = Arrays.asList(
                                                // All points of a face contour.
                                                FirebaseVisionFaceContour.ALL_POINTS,

                                                // The outline of the subject's face.
                                                FirebaseVisionFaceContour.FACE,

                                                // The top outline of the subject's left eyebrow.
                                                FirebaseVisionFaceContour.LEFT_EYEBROW_TOP,
                                                // The bottom outline of the subject's left eyebrow.
                                                FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM,
                                                // The top outline of the subject's right eyebrow.
                                                FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP,
                                                // The bottom outline of the subject's right eyebrow.
                                                FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM,

                                                // The outline of the subject's left eye cavity.
                                                FirebaseVisionFaceContour.LEFT_EYE,
                                                // The outline of the subject's right eye cavity.
                                                FirebaseVisionFaceContour.RIGHT_EYE,

                                                // The top outline of the subject's upper lip.
                                                FirebaseVisionFaceContour.UPPER_LIP_TOP,
                                                // The bottom outline of the subject's upper lip.
                                                FirebaseVisionFaceContour.UPPER_LIP_BOTTOM,
                                                // The top outline of the subject's lower lip.
                                                FirebaseVisionFaceContour.LOWER_LIP_TOP,
                                                // The bottom outline of the subject's lower lip.
                                                FirebaseVisionFaceContour.LOWER_LIP_BOTTOM,

                                                // The outline of the subject's nose bridge.
                                                FirebaseVisionFaceContour.NOSE_BRIDGE,
                                                // The outline of the subject's nose bridge.
                                                FirebaseVisionFaceContour.NOSE_BOTTOM
                                        );

                                        for (@ContourType int contourType : contourTypes) {
                                            JSObject contourObject = contourHelper.get(face, contourType);

                                            if (contourObject.length() > 0) {
                                                contoursArray.put(contourObject);
                                            }
                                        }

                                        if (contoursArray.length() > 0) {
                                            faceObject.put("contours", contoursArray);
                                        }
                                    }

                                    // Returns the rotation of the face about the vertical axis of the image.
                                    faceObject.put("headEulerAngleY", face.getHeadEulerAngleY());
                                    // Returns the rotation of the face about the axis pointing out of the image.
                                    faceObject.put("headEulerAngleZ", face.getHeadEulerAngleZ());

                                    if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                        // Returns a value between 0.0 and 1.0 giving a probability that the face's left eye is open.
                                        faceObject.put("leftEyeOpenProbability", face.getLeftEyeOpenProbability());
                                    }
                                    if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                        // Returns a value between 0.0 and 1.0 giving a probability that the face's right eye is open.
                                        faceObject.put("rightEyeOpenProbability", face.getRightEyeOpenProbability());
                                    }
                                    if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                        // Returns a value between 0.0 and 1.0 giving a probability that the face is smiling.
                                        faceObject.put("smilingProbability", face.getSmilingProbability());
                                    }

                                    if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                        // Returns the tracking ID if the tracking is enabled.
                                        faceObject.put("trackingId", face.getTrackingId());
                                    }

                                    facesArray.put(faceObject);
                                }

                                JSObject data = new JSObject();
                                data.put("faces", facesArray);

                                call.success(data);
                            }
                    )
                    .addOnFailureListener(
                            (Exception e) -> {
                                //Toast.makeText(getContext(), "Unable to detect in image: " + e, Toast.LENGTH_SHORT).show();

                                //Log.i(getLogTag(), "Face detection failed " + e);

                                call.error(e.getLocalizedMessage(), e);
                            }
                    );

            // Closes this FirebaseVisionFaceDetector and releases its model resources.
            faceDetector.close();
        } catch (
                Exception e) {
            call.error(e.getLocalizedMessage(), e);
        }
    }
}
