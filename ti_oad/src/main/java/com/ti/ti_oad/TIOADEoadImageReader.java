package com.ti.ti_oad;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.LogPrinter;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
/**
 * Created by ole on 10/11/2017.
 */

public class TIOADEoadImageReader {

  private final String TAG = TIOADEoadImageReader.class.getSimpleName();

  private byte[] rawImageData;
  public TIOADEoadHeader imageHeader;
  private ArrayList <TIOADEoadHeader.TIOADEoadSegmentInformation> imageSegments;
  private Context context;

  public TIOADEoadImageReader(Uri filename, Context context) {
    this.imageSegments = new ArrayList<>();
    this.context = context;
    this.TIOADToadLoadImageFromDevice(filename);
  }

  public TIOADEoadImageReader(String filename, Context context) {
    this.imageSegments = new ArrayList<>();
    this.context = context;
    this.TIOADToadLoadImage(filename);
  }

  public void TIOADToadLoadImage(String assetFilename) {
    AssetManager aMan = this.context.getAssets();

    try {
      InputStream inputStream = aMan.open(assetFilename);
      rawImageData = new byte[inputStream.available()];

      //Log.i("file", rawImageData);
      int len = inputStream.read(rawImageData);
      Log.d(TAG,"Read " + len + " bytes from asset file");
      this.imageHeader = new TIOADEoadHeader(rawImageData);
      this.imageHeader.validateImage();
    }
    catch (IOException e) {
      Log.d(TAG,"Could not read input file");
    }
  }
  public void TIOADToadLoadImageFromDevice(Uri filename) {
    try {
      InputStream inputStream = context.getContentResolver().openInputStream(filename);
      rawImageData = new byte[inputStream.available()];
      Intent email = new Intent(Intent.ACTION_SEND);
      email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "saidur.shawon@gmail.com"});
      email.putExtra(Intent.EXTRA_SUBJECT, "byte data");
      //Log.i("mklda", Arrays.toString(rawImageData));
      //email.putExtra(Intent.EXTRA_TEXT, Arrays.toString(rawImageData));

//need this to prompts email client only
      email.setType("message/rfc822");

    //  context.startActivity(Intent.createChooser(email, "Choose an Email client :"));
      int len = inputStream.read(rawImageData);
      Log.d("file","Read " + len + " bytes from file");
      Log.i("file", Arrays.toString(rawImageData));
      this.imageHeader = new TIOADEoadHeader(rawImageData);
      this.imageHeader.validateImage();
    }
    catch (IOException e) {
      Log.d(TAG,"Could not read input file");
    }
  }

  public byte[] getRawImageData() {
    return rawImageData;
  }

  public byte[] getHeaderForImageNotify() {
    byte[] imageNotifyHeader = new byte[22];
    int position = 0;
    //0

    Log.i("mkl", String.valueOf("image identification value "+imageHeader.TIOADEoadImageIdentificationValue));
  //  Log.i("mkl", "image identification value "+Base64.getEncoder().encodeToString(imageHeader.TIOADEoadImageIdentificationValue));
    System.arraycopy(imageHeader.TIOADEoadImageIdentificationValue,0,imageNotifyHeader,position,imageHeader.TIOADEoadImageIdentificationValue.length);
    position += imageHeader.TIOADEoadImageIdentificationValue.length;
    //7
    Log.i("mkl", "BIM version "+String.valueOf(imageHeader.TIOADEoadBIMVersion));
    imageNotifyHeader[position++] = imageHeader.TIOADEoadBIMVersion;
    //8
    Log.i("mkl","bim header version"+ String.valueOf(imageHeader.TIOADEoadImageHeaderVersion));
    imageNotifyHeader[position++] = imageHeader.TIOADEoadImageHeaderVersion;
    //9
    Log.i("mkl", "image information"+String.valueOf(imageHeader.TIOADEoadImageInformation));
    System.arraycopy(imageHeader.TIOADEoadImageInformation,0,imageNotifyHeader,position,imageHeader.TIOADEoadImageInformation.length);
    position += imageHeader.TIOADEoadImageInformation.length;
    //13
    for (int ii = 0; ii < 4; ii++) {
      Log.i("mkl", String.valueOf(TIOADEoadDefinitions.GET_BYTE_FROM_UINT32(imageHeader.TIOADEoadImageLength, ii)));
      imageNotifyHeader[position++] = TIOADEoadDefinitions.GET_BYTE_FROM_UINT32(imageHeader.TIOADEoadImageLength, ii);
    }
    //17
    Log.i("mkl","software version "+ String.valueOf(imageHeader.TIOADEoadImageSoftwareVersion));

    System.arraycopy(imageHeader.TIOADEoadImageSoftwareVersion,0,imageNotifyHeader,position,imageHeader.TIOADEoadImageSoftwareVersion.length);
    position += imageHeader.TIOADEoadImageSoftwareVersion.length;
    //21
    //Log.i("mkl", "header made "+ Base64.getEncoder().encodeToString(imageNotifyHeader));
    Log.i("mkl", "header made "+Arrays.toString(imageNotifyHeader));
    return imageNotifyHeader;
  }


}

