// Extended option for controlling additional accuracy / speed trade-offs in performing face detection.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export var PerformanceMode;
(function (PerformanceMode) {
    // Indicates a preference for speed in extended settings that may make an accuracy vs. speed trade-off.
    // This will tend to detect fewer faces and may be less precise in determining values such as position, but will run faster.
    PerformanceMode[PerformanceMode["FAST"] = 1] = "FAST";
    // Indicates a preference for accuracy in extended settings that may make an accuracy vs. speed trade-off.
    // This will tend to detect more faces and may be more precise in determining values such as position, at the cost of speed.
    PerformanceMode[PerformanceMode["ACCURATE"] = 2] = "ACCURATE";
})(PerformanceMode || (PerformanceMode = {}));
// Sets whether to detect no landmarks or all landmarks.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export var LandmarkMode;
(function (LandmarkMode) {
    // Does not perform landmark detection.
    LandmarkMode[LandmarkMode["NO_LANDMARKS"] = 1] = "NO_LANDMARKS";
    // Detects FirebaseVisionFaceLandmark for a given face.
    LandmarkMode[LandmarkMode["ALL_LANDMARKS"] = 2] = "ALL_LANDMARKS";
})(LandmarkMode || (LandmarkMode = {}));
// Landmark types for face.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceLandmark#constant-summary
export var LandmarkType;
(function (LandmarkType) {
    // The center of the subject's bottom lip.
    LandmarkType[LandmarkType["MOUTH_BOTTOM"] = 0] = "MOUTH_BOTTOM";
    // The midpoint between the subject's left mouth corner and the outer corner of the subject's left eye.
    LandmarkType[LandmarkType["LEFT_CHEEK"] = 1] = "LEFT_CHEEK";
    // The midpoint of the subject's left ear tip and left ear lobe.
    LandmarkType[LandmarkType["LEFT_EAR"] = 3] = "LEFT_EAR";
    // The center of the subject's left eye cavity.
    LandmarkType[LandmarkType["LEFT_EYE"] = 4] = "LEFT_EYE";
    // The subject's left mouth corner where the lips meet.
    LandmarkType[LandmarkType["MOUTH_LEFT"] = 5] = "MOUTH_LEFT";
    // The midpoint between the subject's nostrils where the nose meets the face.
    LandmarkType[LandmarkType["NOSE_BASE"] = 6] = "NOSE_BASE";
    // The midpoint between the subject's right mouth corner and the outer corner of the subject's right eye.
    LandmarkType[LandmarkType["RIGHT_CHEEK"] = 7] = "RIGHT_CHEEK";
    // The midpoint of the subject's right ear tip and right ear lobe.
    LandmarkType[LandmarkType["RIGHT_EAR"] = 9] = "RIGHT_EAR";
    // The center of the subject's right eye cavity.
    LandmarkType[LandmarkType["RIGHT_EYE"] = 10] = "RIGHT_EYE";
    // The subject's right mouth corner where the lips meet.
    LandmarkType[LandmarkType["MOUTH_RIGHT"] = 11] = "MOUTH_RIGHT";
})(LandmarkType || (LandmarkType = {}));
// Sets whether to detect contours or not.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export var ContourMode;
(function (ContourMode) {
    // Does not perform contour detection.
    ContourMode[ContourMode["NO_CONTOURS"] = 1] = "NO_CONTOURS";
    // Detects FirebaseVisionFaceContour for a given face.
    // Note that it would return contours for up to 5 faces
    ContourMode[ContourMode["ALL_CONTOURS"] = 2] = "ALL_CONTOURS";
})(ContourMode || (ContourMode = {}));
// Contour types for face.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceContour#constant-summary
export var ContourType;
(function (ContourType) {
    // All points of a face contour.
    ContourType[ContourType["ALL_POINTS"] = 1] = "ALL_POINTS";
    // The outline of the subject's face.
    ContourType[ContourType["FACE"] = 2] = "FACE";
    // The top outline of the subject's left eyebrow.
    ContourType[ContourType["LEFT_EYEBROW_TOP"] = 3] = "LEFT_EYEBROW_TOP";
    // The bottom outline of the subject's left eyebrow.
    ContourType[ContourType["LEFT_EYEBROW_BOTTOM"] = 4] = "LEFT_EYEBROW_BOTTOM";
    // The top outline of the subject's right eyebrow.
    ContourType[ContourType["RIGHT_EYEBROW_TOP"] = 5] = "RIGHT_EYEBROW_TOP";
    // The bottom outline of the subject's right eyebrow.
    ContourType[ContourType["RIGHT_EYEBROW_BOTTOM"] = 6] = "RIGHT_EYEBROW_BOTTOM";
    // The outline of the subject's left eye cavity.
    ContourType[ContourType["LEFT_EYE"] = 7] = "LEFT_EYE";
    // The outline of the subject's right eye cavity.
    ContourType[ContourType["RIGHT_EYE"] = 8] = "RIGHT_EYE";
    // The top outline of the subject's upper lip.
    ContourType[ContourType["UPPER_LIP_TOP"] = 9] = "UPPER_LIP_TOP";
    // The bottom outline of the subject's upper lip.
    ContourType[ContourType["UPPER_LIP_BOTTOM"] = 10] = "UPPER_LIP_BOTTOM";
    // The top outline of the subject's lower lip.
    ContourType[ContourType["LOWER_LIP_TOP"] = 11] = "LOWER_LIP_TOP";
    // The bottom outline of the subject's lower lip.
    ContourType[ContourType["LOWER_LIP_BOTTOM"] = 12] = "LOWER_LIP_BOTTOM";
    // The outline of the subject's nose bridge.
    ContourType[ContourType["NOSE_BRIDGE"] = 13] = "NOSE_BRIDGE";
    // The outline of the subject's nose bridge.
    ContourType[ContourType["NOSE_BOTTOM"] = 14] = "NOSE_BOTTOM";
})(ContourType || (ContourType = {}));
// Indicates whether to run additional classifiers for characterizing attributes such as "smiling" and "eyes open".
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export var ClassificationMode;
(function (ClassificationMode) {
    // Does not perform classification.
    ClassificationMode[ClassificationMode["NO_CLASSIFICATIONS"] = 1] = "NO_CLASSIFICATIONS";
    // Performs "eyes open" and "smiling" classification.
    ClassificationMode[ClassificationMode["ALL_CLASSIFICATIONS"] = 2] = "ALL_CLASSIFICATIONS";
})(ClassificationMode || (ClassificationMode = {}));
//# sourceMappingURL=definitions.js.map