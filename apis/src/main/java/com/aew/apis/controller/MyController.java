package com.aew.apis.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/v1")
@RestController
public class MyController {

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome() {

        return "Welcome to the API´s";
    }

    @RequestMapping(value = "/qr/{text}", method = RequestMethod.GET)
    public File qrCodeGenerator(@PathVariable("text") String text) throws WriterException, IOException {
        String filePath = "";
        String fileType = "png";
        int size = 125;
        // UUID uuid = UUID.randomUUID();
        // String randomUUIDString = uuid.toString();
        QRCodeWriter qrcode = new QRCodeWriter();
        BitMatrix matrix = qrcode.encode(text, BarcodeFormat.QR_CODE, size, size);
        File qrFile = new File(filePath + text/* randomUUIDString */ + "." + fileType);
        int matrixWidth = matrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        graphics.setColor(Color.BLACK);
        for (int b = 0; b < matrixWidth; b++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (matrix.get(b, j)) {
                    graphics.fillRect(b, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
        return qrFile;

    }

    @RequestMapping(value = "/translate/{text}", method = RequestMethod.GET)
    public String translateText(@PathVariable("text") String text) {
        // Need a Key of Google
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        Translation translation = translate.translate(text, TranslateOption.sourceLanguage("es"),
                TranslateOption.targetLanguage("en"));

        return translation.getTranslatedText();
    }

    @RequestMapping(value = "/distance/{text}", method = RequestMethod.GET)
    public DistanceMatrix distance(@PathVariable("text") String text)
            throws ApiException, InterruptedException, IOException {
        // Need a Key of Google
        String API_KEY = "YOUR_API_KEY";

        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
        String originAddresses = "tandil misiones 776";
        String destinationAddresses = "tandil " + text;

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
        DistanceMatrix trix = req.origins(originAddresses).destinations(destinationAddresses).mode(TravelMode.DRIVING)
                .await();
        return trix;

    }

}
