package jadex.android.exampleproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.ByteArrayOutputStream;

import jadex.android.EventReceiver;
import jadex.android.JadexAndroidActivity;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;

/**
 * Hello World Activity.
 * Can Launch a platform and run agents.
 */
public class HelloWorldActivity extends JadexAndroidActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String integerAction = "1234";
    private IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filter = new IntentFilter();
        filter.addAction(integerAction);

        Button camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //wenn der CameraButton geklickt wird dann wird die Kamera aufgerufen und man kann ein Bild machen
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Wenn ein Bild gemacht wurde wird dies als Bundle im Intent data übergeben, welches dann hier in ein Bytearray
        //konvertiert wird
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //Log.d("Test", byteArray.toString() + "    " + byteArray.length);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter());

            Intent intent = new Intent(this, MyJadexService.class);
            intent.putExtra("Bytearray", byteArray);

            startService(intent);

        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(receiver, filter);
        Log.d("Hier","Ich bin jetzt in onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                int anzahl = intent.getIntExtra("anzahl",12345);
                Log.d("Anzahl in der Activity", String.valueOf(anzahl));
                TextView anzahlGesichter = (TextView)findViewById(R.id.anzahlGesichter);
                anzahlGesichter.setText("Anzahl der Gesichter: "+String.valueOf(anzahl));

                byte[] fertigesBild = intent.getByteArrayExtra("fertigesBild");
                Log.d("test2",fertigesBild.toString());
                Bitmap image = BitmapFactory.decodeByteArray(fertigesBild,0,fertigesBild.length);

                ImageView view = (ImageView)findViewById(R.id.imageView);
                view.setImageBitmap(image);

            }
    };

    public boolean onCreateOptionsMenu(Menu menu)
    {
         MenuItem controlCenterMenuItem = menu.add(0, 0, 0, "Control Center");
        controlCenterMenuItem.setIcon(android.R.drawable.ic_menu_manage);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == 0)
            {
                Intent i = new Intent(this, JadexAndroidControlCenter.class);
                i.putExtra("platformId", (ComponentIdentifier) platformId);
                startActivity(i);

            }
        return true;
    }
    // END -------- show control center in menu ---------

}
