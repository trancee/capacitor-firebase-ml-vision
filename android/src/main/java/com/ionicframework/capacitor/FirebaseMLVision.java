package com.ionicframework.capacitor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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
        void get(FirebaseVisionFace face, String type, int landmarkType, JSArray landmarksArray);
    }

    LandmarkHelper landmarkHelper = (face, type, landmarkType, landmarksArray) -> {
        // Represent a face landmark. A landmark is a point on a detected face, such as an eye, nose, or mouth.
        // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceLandmark
        FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkType);

        if (landmark != null) {
            // Represent a 2D or 3D point for FirebaseVision.
            // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/common/FirebaseVisionPoint
            FirebaseVisionPoint point = landmark.getPosition();

            JSObject pointObject = pointHelper.get(point);

            JSObject landmarkObject = new JSObject();

            // Gets the FirebaseVisionFaceLandmark.LandmarkType type.
            landmarkObject.put("type", landmark.getLandmarkType());
            // Gets a 2D point for landmark position, where (0, 0) is the upper-left corner of the image.
            landmarkObject.put("position", pointObject);

            landmarksArray.put(landmarkObject);
        }
    };

    private interface ContourHelper {
        void get(FirebaseVisionFace face, String type, int contourType, JSArray contoursArray);
    }

    ContourHelper contourHelper = (face, type, contourType, contoursArray) -> {
        // Represent a face contour. A contour is a list of points on a detected face, such as the mouth.
        // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceContour
        FirebaseVisionFaceContour contour = face.getContour(contourType);

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

                JSObject contourObject = new JSObject();

                // Gets the FirebaseVisionFaceContour.ContourType type.
                contourObject.put("type", contour.getFaceContourType());
                // Gets a list of 2D points for this face contour, where (0, 0) is the upper-left corner of the image.
                contourObject.put("points", pointsArray);

                contoursArray.put(contourObject);
            }
        }
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

                                    // Returns the axis-aligned bounding rectangle of the detected face.
                                    Rect bounds = face.getBoundingBox();

                                    {
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

                                        // The midpoint between the subject's left mouth corner and the outer corner of the subject's left eye.
                                        landmarkHelper.get(face, "cheek", FirebaseVisionFaceLandmark.LEFT_CHEEK, landmarksArray);
                                        // The midpoint between the subject's right mouth corner and the outer corner of the subject's right eye.
                                        landmarkHelper.get(face, "cheek", FirebaseVisionFaceLandmark.RIGHT_CHEEK, landmarksArray);
                                        // The midpoint of the subject's left ear tip and left ear lobe.
                                        landmarkHelper.get(face, "ear", FirebaseVisionFaceLandmark.LEFT_EAR, landmarksArray);
                                        // The midpoint of the subject's right ear tip and right ear lobe.
                                        landmarkHelper.get(face, "ear", FirebaseVisionFaceLandmark.RIGHT_EAR, landmarksArray);
                                        // The center of the subject's left eye cavity.
                                        landmarkHelper.get(face, "eye", FirebaseVisionFaceLandmark.LEFT_EYE, landmarksArray);
                                        // The midpoint of the subject's right ear tip and right ear lobe.
                                        landmarkHelper.get(face, "eye", FirebaseVisionFaceLandmark.RIGHT_EYE, landmarksArray);
                                        // The midpoint between the subject's nostrils where the nose meets the face.
                                        landmarkHelper.get(face, "nose", FirebaseVisionFaceLandmark.NOSE_BASE, landmarksArray);
                                        // The center of the subject's bottom lip.
                                        landmarkHelper.get(face, "mouth", FirebaseVisionFaceLandmark.MOUTH_BOTTOM, landmarksArray);
                                        // The subject's left mouth corner where the lips meet.
                                        landmarkHelper.get(face, "mouth", FirebaseVisionFaceLandmark.MOUTH_LEFT, landmarksArray);
                                        // The subject's right mouth corner where the lips meet.
                                        landmarkHelper.get(face, "mouth", FirebaseVisionFaceLandmark.MOUTH_RIGHT, landmarksArray);

                                        if (landmarksArray.length() > 0) {
                                            faceObject.put("landmarks", landmarksArray);
                                        }
                                    }

                                    {
                                        JSArray contoursArray = new JSArray();

                                        // All points of a face contour.
                                        contourHelper.get(face, "all", FirebaseVisionFaceContour.ALL_POINTS, contoursArray);
                                        // The outline of the subject's face.
                                        contourHelper.get(face, "face", FirebaseVisionFaceContour.FACE, contoursArray);
                                        // The top outline of the subject's left eyebrow.
                                        contourHelper.get(face, "eyebrow", FirebaseVisionFaceContour.LEFT_EYEBROW_TOP, contoursArray);
                                        // The bottom outline of the subject's left eyebrow.
                                        contourHelper.get(face, "eyebrow", FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM, contoursArray);
                                        // The top outline of the subject's right eyebrow.
                                        contourHelper.get(face, "eyebrow", FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP, contoursArray);
                                        // The bottom outline of the subject's right eyebrow.
                                        contourHelper.get(face, "eyebrow", FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM, contoursArray);
                                        // The outline of the subject's left eye cavity.
                                        contourHelper.get(face, "eye", FirebaseVisionFaceContour.LEFT_EYE, contoursArray);
                                        // The outline of the subject's right eye cavity.
                                        contourHelper.get(face, "eye", FirebaseVisionFaceContour.RIGHT_EYE, contoursArray);
                                        // The top outline of the subject's upper lip.
                                        contourHelper.get(face, "lip", FirebaseVisionFaceContour.UPPER_LIP_TOP, contoursArray);
                                        // The bottom outline of the subject's upper lip.
                                        contourHelper.get(face, "lip", FirebaseVisionFaceContour.UPPER_LIP_BOTTOM, contoursArray);
                                        // The top outline of the subject's lower lip.
                                        contourHelper.get(face, "lip", FirebaseVisionFaceContour.LOWER_LIP_TOP, contoursArray);
                                        // The bottom outline of the subject's lower lip.
                                        contourHelper.get(face, "lip", FirebaseVisionFaceContour.LOWER_LIP_BOTTOM, contoursArray);
                                        // The outline of the subject's nose bridge.
                                        contourHelper.get(face, "nose", FirebaseVisionFaceContour.NOSE_BRIDGE, contoursArray);
                                        // The outline of the subject's nose bridge.
                                        contourHelper.get(face, "nose", FirebaseVisionFaceContour.NOSE_BOTTOM, contoursArray);

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
