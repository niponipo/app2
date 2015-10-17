package jadex.android.exampleproject;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

/**
 * Created by Nico on 07.08.2015.
 */
public interface IAgentInterface {

    void erkenneGesicht(byte[] bytearray);

}
