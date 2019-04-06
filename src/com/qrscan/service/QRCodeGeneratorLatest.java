
package com.qrscan.service;

import com.backendless.Backendless;
import com.backendless.servercode.BackendlessService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
 
@BackendlessService
public class QRCodeGeneratorLatest
{
 // in this method the work mainly is with the 'ZXing' library
 public byte[] generateQRCode( String data, int width, int height ) throws IOException, WriterException
 {
   QRCodeWriter qrCodeWriter = new QRCodeWriter();
   BitMatrix bitMatrix = qrCodeWriter.encode( data, BarcodeFormat.QR_CODE, width, height );
 
   byte[] png;
   try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
   {
     MatrixToImageWriter.writeToStream( bitMatrix, "PNG", baos );
     png = baos.toByteArray();
   }
   return png;
 }
 
 public HashMap<String, String> generateQRCodePicture( String data, int width, int height ) throws WriterException, IOException
 {
  // generate qr code with our method
  byte[] png = this.generateQRCode( data, width, height );
 
  // generate random uuid as file name and save it using Backendless File service
  String uuid = UUID.randomUUID().toString();
  String path = Backendless.Files.saveFile( "qr_codes", uuid + ".png", png );
 
  // return response with the raw data (that was encoded) and file path (to qr code)
  HashMap<String, String> record = new HashMap<>();
  record.put( "data", data );
  record.put( "file", path );
  Map<String, Object> result = Backendless.Data.of( "qr_codes" ).save( record );
  return record;
 }
 
 public HashMap<String, String> generateQRCodeForLogin() throws IOException, WriterException
 {
   return generateQRCodePicture( UUID.randomUUID().toString(), 250, 250 );
 }
 
 public String getUserId()
 {
  return Backendless.UserService.loggedInUser();
 }
}