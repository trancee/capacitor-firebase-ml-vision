import { WebPlugin } from '@capacitor/core';
import { FirebaseMLVisionPlugin } from './definitions';
import { FirebaseVisionFaceDetectorOptions, FirebaseVisionFaceResult } from './definitions';
export declare class FirebaseMLVisionWeb extends WebPlugin implements FirebaseMLVisionPlugin {
    constructor();
    detectInImage(options: {
        image: string;
        options?: FirebaseVisionFaceDetectorOptions;
    }): Promise<FirebaseVisionFaceResult>;
}
declare const FirebaseMLVision: FirebaseMLVisionWeb;
export { FirebaseMLVision };
