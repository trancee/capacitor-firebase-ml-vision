declare module "@capacitor/core" {
    interface PluginRegistry {
        FirebaseMLVision: FirebaseMLVisionPlugin;
    }
}
export interface FirebaseVisionFaceDetectorOptions {
    performanceMode?: PerformanceMode;
    landmarkMode?: LandmarkMode;
    contourMode?: ContourMode;
    classificationMode?: ClassificationMode;
    minFaceSize?: number;
    enableTracking?: boolean;
}
export interface FirebaseVisionPoint {
    x: number;
    y: number;
    z: number;
}
export interface FirebaseVisionFaceContour {
    type: ContourType;
    points: FirebaseVisionPoint[];
}
export interface FirebaseVisionFaceLandmark {
    type: LandmarkType;
    position: FirebaseVisionPoint;
}
export interface Rect {
    left: number;
    x: number;
    top: number;
    y: number;
    right: number;
    bottom: number;
    width: number;
    height: number;
}
export interface FirebaseVisionFace {
    bounds: Rect;
    landmarks?: FirebaseVisionFaceLandmark[];
    contours?: FirebaseVisionFaceContour[];
    headEulerAngleY: number;
    headEulerAngleZ: number;
    leftEyeOpenProbability?: number;
    rightEyeOpenProbability?: number;
    smilingProbability?: number;
    trackingId?: number;
}
export interface FirebaseVisionFaceResult {
    faces: FirebaseVisionFace[];
}
export interface FirebaseMLVisionPlugin {
    detectInImage(options: {
        image: string;
        options?: FirebaseVisionFaceDetectorOptions;
    }): Promise<FirebaseVisionFaceResult>;
}
export declare enum PerformanceMode {
    FAST = 1,
    ACCURATE = 2
}
export declare enum LandmarkMode {
    NO_LANDMARKS = 1,
    ALL_LANDMARKS = 2
}
export declare enum LandmarkType {
    MOUTH_BOTTOM = 0,
    LEFT_CHEEK = 1,
    LEFT_EAR = 3,
    LEFT_EYE = 4,
    MOUTH_LEFT = 5,
    NOSE_BASE = 6,
    RIGHT_CHEEK = 7,
    RIGHT_EAR = 9,
    RIGHT_EYE = 10,
    MOUTH_RIGHT = 11
}
export declare enum ContourMode {
    NO_CONTOURS = 1,
    ALL_CONTOURS = 2
}
export declare enum ContourType {
    ALL_POINTS = 1,
    FACE = 2,
    LEFT_EYEBROW_TOP = 3,
    LEFT_EYEBROW_BOTTOM = 4,
    RIGHT_EYEBROW_TOP = 5,
    RIGHT_EYEBROW_BOTTOM = 6,
    LEFT_EYE = 7,
    RIGHT_EYE = 8,
    UPPER_LIP_TOP = 9,
    UPPER_LIP_BOTTOM = 10,
    LOWER_LIP_TOP = 11,
    LOWER_LIP_BOTTOM = 12,
    NOSE_BRIDGE = 13,
    NOSE_BOTTOM = 14
}
export declare enum ClassificationMode {
    NO_CLASSIFICATIONS = 1,
    ALL_CLASSIFICATIONS = 2
}
