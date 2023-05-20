package com.example.currencybotapp.bot;

import com.example.currencybotapp.config.BotConfig;
import com.example.currencybotapp.model.MessageModel;
import com.example.currencybotapp.repository.MessageRepository;
import com.example.currencybotapp.service.CurrencyService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CurrencyService currencyService;
    private final MessageRepository messageRepository;

    public TelegramBot(BotConfig botConfig, CurrencyService currencyService, MessageRepository messageRepository) {
        this.botConfig = botConfig;
        this.currencyService = currencyService;
        this.messageRepository = messageRepository;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String currency = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            // Сохранение сообщений в базе данных
            MessageModel message = new MessageModel();
            message.setChatId(chatId);
            message.setUserId(userId);
            message.setMessageText(messageText);
            message.setMessageType("text"); // текстовые сообщения
            messageRepository.save(message);

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    String[] currencyParts = messageText.split("to");
                    if (currencyParts.length == 2) {
                        String fromCurrency = currencyParts[0].trim();
                        String toCurrency = currencyParts[1].trim();
                        try {
                            currency = currencyService.getCurrencyRate(fromCurrency, toCurrency, 1.0);
                        } catch (Exception e) {
                            currency = "Не удалось получить курс валюты. Пожалуйста, попробуйте еще раз.";
                        }
                    } else {
                        currency = "Неверный формат валюты. Введите валюту в правильном формате, например: USD to KZT 1.";
                    }
                    sendMessage(chatId, currency);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + ", Приятно познакомится" + "\n" +
                "Введите валюту, официальный курс которой\n" +
                "вы хотите" + "\n" +
                "Например: USD to KZT 10(количество)" + "\n";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
