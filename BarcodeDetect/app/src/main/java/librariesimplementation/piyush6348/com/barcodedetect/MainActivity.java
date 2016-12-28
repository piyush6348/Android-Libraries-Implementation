package librariesimplementation.piyush6348.com.barcodedetect;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    public static int PROFILE_PIC_COUNT=0;
    private Button btn;
    private ImageView imageView;
    private TextView textView;
    final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
    private static Bitmap setphoto;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if(requestCode==REQUEST_CAMERA)
            {
                if(data!=null)
                {
                     setphoto = (Bitmap) data.getExtras().get("data");
                    //photo = Bitmap.createScaledBitmap(photo, 80, 80, false);
                    imageView.setImageBitmap(setphoto);
                }
            }
            else if(requestCode==SELECT_FILE)
            {
                this.imageFromGallery(resultCode, data);

                imageView.setImageBitmap(null);

                if(setphoto!=null)
                imageView.setImageBitmap(setphoto);
            }
            getBarCode();
        }
    }

    private void imageFromGallery(int resultCode, Intent data) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(data.getData());
            setphoto=BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=(Button)findViewById(R.id.button_camera);
        imageView=(ImageView)findViewById(R.id.imgview);
        textView=(TextView)findViewById(R.id.text_view);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Take Photo")) {
                            PROFILE_PIC_COUNT = 1;
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose from Library")) {
                            PROFILE_PIC_COUNT = 1;
                            Intent intent = new Intent(
                                    Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent,SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            PROFILE_PIC_COUNT = 0;
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

    }

    void getBarCode()
    {
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if(!detector.isOperational()){
            textView.setText("Could not set up the detector!");
        }
        else
        {
            Frame frame = new Frame.Builder().setBitmap(setphoto).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            if(barcodes.size()>0)
            {
                Barcode thisCode = barcodes.valueAt(0);
                textView.setText(thisCode.rawValue);
            }
        }
    }
}
