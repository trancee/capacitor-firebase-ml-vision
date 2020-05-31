declare module "@capacitor/core" {
  interface PluginRegistry {
    FirebaseMLVision: FirebaseMLVisionPlugin;
  }
}

// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions
export interface FirebaseVisionFaceDetectorOptions {
  // Extended option for controlling additional accuracy / speed trade-offs in performing face detection.
  // In general, choosing the more accurate mode will generally result in longer runtime, whereas choosing the faster mode will generally result in detecting fewer faces.
  // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.PerformanceMode
  performanceMode?: PerformanceMode,
  // Sets whether to detect no landmarks or all landmarks.
  // Processing time increases as the number of landmarks to search for increases, so detecting all landmarks will increase the overall detection time.
  // Detecting landmarks can improve pose estimation.
  // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.LandmarkMode
  landmarkMode?: LandmarkMode,
  // Sets whether to detect contours or not.
  // Processing time increases as the number of contours to search for increases, so detecting all contours will increase the overall detection time.
  // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.ContourMode
  contourMode?: ContourMode,
  // Indicates whether to run additional classifiers for characterizing attributes such as "smiling" and "eyes open".
  // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.ClassificationMode
  classificationMode?: ClassificationMode,
  // Sets the smallest desired face size, expressed as a proportion of the width of the head to the image width.
  // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.Builder#public-firebasevisionfacedetectoroptions.builder-setminfacesize-float-minfacesize
  minFaceSize?: number,
  // Enables face tracking, which will maintain a consistent ID for each face when processing consecutive frames.
  // Tracking should be disabled for handling a series of non-consecutive still images.
  // https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions.Builder#public-firebasevisionfacedetectoroptions.builder-enabletracking
  enableTracking?: boolean,
}

// Represent a 2D or 3D point for FirebaseVision.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/common/FirebaseVisionPoint
export interface FirebaseVisionPoint {
  // Gets x coordinate.
  x: number,
  // Gets y coordinate.
  y: number,
  // Gets z coordinate (or depth).
  z: number,
}

// Represent a face contour.
// A contour is a list of points on a detected face, such as the mouth.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceContour
export interface FirebaseVisionFaceContour {
  // Gets the FirebaseVisionFaceContour.ContourType type.
  type: ContourType,
  // Gets a list of 2D points for this face contour, where (0, 0) is the upper-left corner of the image.
  points: FirebaseVisionPoint[],
}

// Represent a face landmark.
// A landmark is a point on a detected face, such as an eye, nose, or mouth.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceLandmark
export interface FirebaseVisionFaceLandmark {
  // Gets the FirebaseVisionFaceLandmark.LandmarkType type.
  type: LandmarkType,
  // Gets a 2D point for landmark position, where (0, 0) is the upper-left corner of the image.
  position: FirebaseVisionPoint,
}

// https://developer.android.com/reference/android/graphics/Rect.html
export interface Rect {
  // The X coordinate of the left side of the rectangle
  left: number,
  x: number,
  // The Y coordinate of the top of the rectangle
  top: number,
  y: number,
  // The X coordinate of the right side of the rectangle
  right: number,
  // The Y coordinate of the bottom of the rectangle
  bottom: number,
  // The rectangle's width
  width: number,
  // The rectangle's height
  height: number,
}

export interface FirebaseVisionFace {
  // Returns the axis-aligned bounding rectangle of the detected face.
  bounds: Rect,
  // Returns a list of face landmarks.
  landmarks?: FirebaseVisionFaceLandmark[],
  // Returns a list of face contours.
  contours?: FirebaseVisionFaceContour[],
  // Returns the rotation of the face about the vertical axis of the image.
  headEulerAngleY: number,
  // Returns the rotation of the face about the axis pointing out of the image.
  headEulerAngleZ: number,
  // Returns a value between 0.0 and 1.0 giving a probability that the face's left eye is open.
  leftEyeOpenProbability?: number,
  // Returns a value between 0.0 and 1.0 giving a probability that the face's right eye is open.
  rightEyeOpenProbability?: number,
  // Returns a value between 0.0 and 1.0 giving a probability that the face is smiling.
  smilingProbability?: number,
  // Returns the tracking ID if the tracking is enabled.
  trackingId?: number,
}

export interface FirebaseVisionFaceResult {
  // Returns a list of detected FirebaseVisionFaces.
  faces: FirebaseVisionFace[],
}

export interface FirebaseMLVisionPlugin {
  // Detects human faces from the supplied image.
  detectInImage(options: {
    // Represents an image object that can be used for both on-device and cloud API detectors.
    image: string,
    // The options for the face detector.
    options?: FirebaseVisionFaceDetectorOptions,
  }): Promise<FirebaseVisionFaceResult>;
}

// Extended option for controlling additional accuracy / speed trade-offs in performing face detection.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export enum PerformanceMode {
  // Indicates a preference for speed in extended settings that may make an accuracy vs. speed trade-off.
  // This will tend to detect fewer faces and may be less precise in determining values such as position, but will run faster.
  FAST = 1,
  // Indicates a preference for accuracy in extended settings that may make an accuracy vs. speed trade-off.
  // This will tend to detect more faces and may be more precise in determining values such as position, at the cost of speed.
  ACCURATE = 2,
}

// Sets whether to detect no landmarks or all landmarks.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export enum LandmarkMode {
  // Does not perform landmark detection.
  NO_LANDMARKS = 1,
  // Detects FirebaseVisionFaceLandmark for a given face.
  ALL_LANDMARKS = 2,
}

// Landmark types for face.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceLandmark#constant-summary
export enum LandmarkType {
  // The center of the subject's bottom lip.
  MOUTH_BOTTOM = 0,
  // The midpoint between the subject's left mouth corner and the outer corner of the subject's left eye.
  LEFT_CHEEK = 1,
  // The midpoint of the subject's left ear tip and left ear lobe.
  LEFT_EAR = 3,
  // The center of the subject's left eye cavity.
  LEFT_EYE = 4,
  // The subject's left mouth corner where the lips meet.
  MOUTH_LEFT = 5,
  // The midpoint between the subject's nostrils where the nose meets the face.
  NOSE_BASE = 6,
  // The midpoint between the subject's right mouth corner and the outer corner of the subject's right eye.
  RIGHT_CHEEK = 7,
  // The midpoint of the subject's right ear tip and right ear lobe.
  RIGHT_EAR = 9,
  // The center of the subject's right eye cavity.
  RIGHT_EYE = 10,
  // The subject's right mouth corner where the lips meet.
  MOUTH_RIGHT = 11,
}

// Sets whether to detect contours or not.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export enum ContourMode {
  // Does not perform contour detection.
  NO_CONTOURS = 1,
  // Detects FirebaseVisionFaceContour for a given face.
  // Note that it would return contours for up to 5 faces
  ALL_CONTOURS = 2,
}

// Contour types for face.
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceContour#constant-summary
export enum ContourType {
  // All points of a face contour.
  ALL_POINTS = 1,
  // The outline of the subject's face.
  FACE = 2,
  // The top outline of the subject's left eyebrow.
  LEFT_EYEBROW_TOP = 3,
  // The bottom outline of the subject's left eyebrow.
  LEFT_EYEBROW_BOTTOM = 4,
  // The top outline of the subject's right eyebrow.
  RIGHT_EYEBROW_TOP = 5,
  // The bottom outline of the subject's right eyebrow.
  RIGHT_EYEBROW_BOTTOM = 6,
  // The outline of the subject's left eye cavity.
  LEFT_EYE = 7,
  // The outline of the subject's right eye cavity.
  RIGHT_EYE = 8,
  // The top outline of the subject's upper lip.
  UPPER_LIP_TOP = 9,
  // The bottom outline of the subject's upper lip.
  UPPER_LIP_BOTTOM = 10,
  // The top outline of the subject's lower lip.
  LOWER_LIP_TOP = 11,
  // The bottom outline of the subject's lower lip.
  LOWER_LIP_BOTTOM = 12,
  // The outline of the subject's nose bridge.
  NOSE_BRIDGE = 13,
  // The outline of the subject's nose bridge.
  NOSE_BOTTOM = 14,
}

// Indicates whether to run additional classifiers for characterizing attributes such as "smiling" and "eyes open".
// https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/face/FirebaseVisionFaceDetectorOptions#constants
export enum ClassificationMode {
  // Does not perform classification.
  NO_CLASSIFICATIONS = 1,
  // Performs "eyes open" and "smiling" classification.
  ALL_CLASSIFICATIONS = 2,
}
