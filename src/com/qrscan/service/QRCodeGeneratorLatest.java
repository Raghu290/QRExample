
package com.qrscan.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;
import com.google.zxing.BarcodeFormat;

import com.backendless.Backendless;
import com.backendless.servercode.BackendlessService;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
/*Sample Code for generating unique QR code on web Page. 
 * This should be used by APP which is being developed for login with scan*/
/*Sai Raghu*/

// This class is annotated with @Backendless. This is marker interface which tells to use public method of this class to expose APIs
@BackendlessService
public class QRCodeGeneratorLatest
{
/*Overloaded method to generate QR.png with given data. This png file will be stored in backendless profile /files*/
 public byte[] getMyQRCode( String data, int width, int height ) throws Exception
 {
   QRCodeWriter writer = new QRCodeWriter();
   byte[] pngByteArray;
 //  QRCodeWriter pgwriter = new QRCodeWriter();
   
   BitMatrix dataMatrix = writer.encode( data, BarcodeFormat.QR_CODE, width, height );
 
   
   try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
   {
     MatrixToImageWriter.writeToStream( dataMatrix, "PNG", stream );
     pngByteArray = stream.toByteArray();
   }
   return pngByteArray;
 }
 
 public HashMap<String, String> generateMyQR( String qrdata, int wth, int ht ) throws Exception
 {
  
  byte[] pngByteArray = this.getMyQRCode( qrdata, wth, ht );
 
  // This will push the .png to file system of backendless
  String generatedRandomImageName = UUID.randomUUID().toString();
  String filePath = Backendless.Files.saveFile( "qr_codes", generatedRandomImageName + ".png", pngByteArray );
 
  // return response with the raw data (that was encoded) and file path (to qr code)
  HashMap<String, String> map = new HashMap<>();
  map.put( "data", qrdata );
  map.put( "file", filePath );
  // saving the details to table qr_codes
  Map<String, Object> result = Backendless.Data.of( "qr_codes" ).save( map );
  return map;
 }
 
 public HashMap<String, String> getLoginQRCode() throws Exception
 {
	 /*This method is triggered by web html for get QR to login*/
   return generateMyQR( UUID.randomUUID().toString(), 250, 250 );
 }
 
 //This is basic backendless service, which give the userid of logged user in App.user to be registered .
 public String getUserId()
 {
  return Backendless.UserService.loggedInUser();
 }
}