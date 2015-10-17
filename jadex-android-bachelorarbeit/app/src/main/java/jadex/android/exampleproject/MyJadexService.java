package jadex.android.exampleproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import jadex.android.service.JadexPlatformManager;
import jadex.android.service.JadexPlatformService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.DefaultResultListener;

/**
 * Created by Nico on 02.08.2015.
 */
@Security(Security.PASSWORD)
public class MyJadexService extends JadexPlatformService {

    Bitmap image;
    Mat mat;
    byte[] bytearray;
    CascadeClassifier faceDetector;
    File mCascadeFile;
    String pfad;
    //private Handler handler;

    // JadexPlatform wird gestartet
    public MyJadexService()
    {
        setPlatformAutostart(true);
        setPlatformName("JadexPlatform");
        setPlatformKernels(JadexPlatformManager.KERNEL_MICRO);
        setPlatformOptions("-awareness true");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

   /* @Override
    public void onCreate()
    {
        super.onCreate();
        this.handler = new Handler(); // needed to make Toasts on UI Thread.

        registerEventReceiver(new EventReceiver<MyEvent>(MyEvent.class)
        {

            public void receiveEvent(final MyEvent event)
            {
                handler.post(new Runnable()
                {

                    public void run()
                    {
                       // Toast.makeText(MyJadexService.this,"Gesichter = "+ event.getAnzahl().toString(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Hier wird das Bild aus dem durchgereichten Bundle extrahiert
        bytearray = intent.getByteArrayExtra("Bytearray");


        return START_STICKY;

    }

    @Override
    protected void onPlatformStarted(IExternalAccess result)
    {
        super.onPlatformStarted(result);

        //Wenn die Platform getstartet ist dann wird er Agent initialisiert
        startMicroAgent("MyAgent", MyAgent.class).addResultListener(new DefaultResultListener<IComponentIdentifier>() {
            @Override
            public void resultAvailable(IComponentIdentifier iComponentIdentifier)
            {
                // Diese Abfrage muss sein sonst funktioniert OpenCV nicht
                if (!OpenCVLoader.initDebug())
                {
                    Log.d("OpenCV", "nicht initialisiert");
                }
                //Hier wird OpenCV initialisiert
                initializeOpenCVDependencies();

                //Hier wird das byteArray in ein Mat konvertiert
                image = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
                mat = new Mat();
                Utils.bitmapToMat(image, mat);

                //1. Gesichtserkennung (funktioniert)
                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(mat, faceDetections);

                Log.d("Anzahl1 im Service", String.valueOf(faceDetections.toArray().length));

                try
                {
                    IAgentInterface agent = getsService(IAgentInterface.class);

                    //Hier wird die Methode erkenneGesicht() aufgerufen, es wird das Mat und
                    //und der faceDectector übergeben, der in initializeOpenCVDependencies() erzeugt wird
                    agent.erkenneGesicht(bytearray);
                } catch (RuntimeException e)
                {
                    Log.e("Fehler", "Hier ist der Fehler");

                }

            }
        });
    }


    public void initializeOpenCVDependencies()
    {
        try
        {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);




            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            pfad = mCascadeFile.getAbsolutePath();


            // Load the cascade classifier
           faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e)
        {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }


    }
}
