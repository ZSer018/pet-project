package bot.service.command.portfolio;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.*;

public class ShowPortfolio extends ResponseService {

    private HashMap<String, String> viewingList = null;
    private boolean edited = false;

    public ShowPortfolio(HashMap<String, String> viewingList) {
        this.viewingList = viewingList;
        if (viewingList != null) {
            edited = true;
        }
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();


        if (viewingList == null) {
            if (dataManager.getPortfolioImgByType(dataManager.getUserViewType(chatId)) != null) {
                viewingList = new HashMap<>(dataManager.getPortfolioImgByType(dataManager.getUserViewType(chatId)));
                dataManager.setUserViewingList(chatId, viewingList);
            }
        }

        if (viewingList == null || viewingList.size() == 0) {
            if (update.hasCallbackQuery()){
                return List.of(new SimpleEditMessage("Это сообщение устарело").getNewEditMessage(update));
            }
            return List.of(new SimpleSendMessage("К сожалению в данном разделе пока нет ни одной фотографии",0).getNewMessage(update));
        }


        if (viewingList.size() == 1){
            return List.of(sendPic(chatId, (String)viewingList.values().toArray()[0]));
        }

        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatId);

        var result = new ArrayList<PartialBotApiMethod<? extends Serializable>>();
        var images = new ArrayList<InputMedia>();

        int x = 10;

        Iterator<String> iterator = viewingList.keySet().iterator();
        while (iterator.hasNext()){
            String fileUniqueId = iterator.next();
            String fileId = viewingList.get(fileUniqueId);
            iterator.remove();
            images.add(new InputMediaPhoto(fileId));
            x--;

            if (x == 0){
                break;
            }
        }

        sendMediaGroup.setMedias(images);
        System.out.println(edited);
        if (edited){
            System.out.println("YES");
            if (viewingList.size() != 0) {
                result.add(new SimpleEditMessage("Далее >>").getNewEditMessage(update));
            } else
                result.add(new SimpleEditMessage("Последние работы").getNewEditMessage(update));
        }

        result.add(sendMediaGroup);

        if (viewingList.size() == 0) {
            result.add(new SimpleSendMessage("В данном разделе больше нет фотографий", 0).getNewMessage(update));
        }

        if (images.size() == 10 & viewingList.size()>0)  {
            result.add(nextPartOfferMessage(chatId));
        }

        return result;
    }

    private SendPhoto sendPic(long chat_id, String fileId){
        SendPhoto picMsg = new SendPhoto();
        picMsg.setChatId(chat_id);
        picMsg.setPhoto(new InputFile(fileId));
        picMsg.setCaption("Больше в этом разделе фотографий у сожалению нет");
        return picMsg;
    }

    private SendMessage nextPartOfferMessage(long chatId) {
        SendMessage message = new SendMessage();
        String answer = "В этом разделе есть еще изображения";
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Показать еще...", "viewing_ShowMore"));
        rowsInline.add(rowInline);

        rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Возможно позже", "viewing_later"));
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        message.setChatId(chatId);
        message.setText(answer);
        return message;
    }

}



