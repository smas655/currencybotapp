package com.example.currencybotapp.util;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class VoiceToTextConverter {

    public String convertVoiceToText(byte[] voiceData) throws IOException {
        String jsonKeyFilePath = "<C:/Users/Nauryzbek/Downloads/currency-account-file.json>";

        // Создаем клиента Google Cloud Speech-to-Text API
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(voiceData);

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("ru-RU")
                    .build();
            RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse recognizeResponse = speechClient.recognize(recognitionConfig, recognitionAudio);
            for (SpeechRecognitionResult result : recognizeResponse.getResultsList()) {
                String transcript = result.getAlternatives(0).getTranscript();
                return transcript;
            }
        }

        return null;
    }
}
