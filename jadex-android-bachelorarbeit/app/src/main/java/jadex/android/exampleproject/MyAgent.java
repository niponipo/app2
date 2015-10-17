package jadex.android.exampleproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.context.IContextService;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * Created by Nico on 07.08.2015.
 */

@ProvidedServices({@ProvidedService(name = "agentinterface", type = IAgentInterface.class)})
//@RequiredServices({@RequiredService(name="context", type=IContextService.class, binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM))})

//@Service
@Agent
public class MyAgent  implements IAgentInterface {



    //@AgentService
    //protected IContextService context;

    //Hier läuft die eigentliche Gesichtserkenung ab, die Anzahl der Gesichter soll hier erstmal
    //im LogCat ausgegeben werden
    @Override
    public void erkenneGesicht(byte[] bytearray)
    {
        CascadeClassifier faceDetector;

        Bitmap image = BitmapFactory.decodeByteArray(bytearray,0,bytearray.length);
        Mat mat = new Mat();
        Utils.bitmapToMat(image,mat);
        faceDetector = new CascadeClassifier("/data/data/jadex.android.exampleproject/app_cascade/lbpcascade_frontalface.xml");

        //2. Gesichtserkennung (funktioniert nicht, immer 0 Gesichter)
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mat, faceDetections);

        Log.d("Anzahl2 im Agent", String.valueOf(faceDetections.toArray().length));

       /* MyEvent myEvent = new MyEvent();
        myEvent.setAnzahl(anzahl);
        context.dispatchEvent(myEvent);*/


    }

}
