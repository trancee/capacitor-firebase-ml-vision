import { WebPlugin } from '@capacitor/core';
import { FirebaseMLVisionPlugin } from './definitions';

import {
  FirebaseVisionFaceDetectorOptions,
  FirebaseVisionFaceResult,
} from './definitions';

export class FirebaseMLVisionWeb extends WebPlugin implements FirebaseMLVisionPlugin {
  constructor() {
    super({
      name: 'FirebaseMLVision',
      platforms: ['web']
    });
  }

  // Detects human faces from the supplied image.
  async detectInImage(options: {
    // Represents an image object that can be used for both on-device and cloud API detectors.
    image: string,
    // The options for the face detector.
    options?: FirebaseVisionFaceDetectorOptions,
  }): Promise<FirebaseVisionFaceResult> {
    console.log("detectInImage", options);
    throw new Error("Method not implemented.");
  }
}

const FirebaseMLVision = new FirebaseMLVisionWeb();

export { FirebaseMLVision };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(FirebaseMLVision);
