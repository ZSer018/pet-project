package bot.service.admin;
import bot.service.ResponseService;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class AddDeletePhoto extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        if (dataManager.getAdmin().getTelegramId() != update.getMessage().getChatId()){
            return List.of(new SimpleSendMessage("Загрузка фотографий пользователем не предусмотрена. \nИзвините.", 0).getNewMessage(update));
        }

        return List.of(savePhoto(update));
    }

    public SendMessage savePhoto(Update update){
        List<PhotoSize> photos = update.getMessage().getPhoto();
        photos.forEach(System.out::println);

        String fileId = photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileId();
        int fileSize = photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileSize();

        int height = photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getHeight();
        int wight = photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getWidth();

        String fileUniqueId =  "_"+fileSize+height+wight;
        System.out.println(fileUniqueId);

        /*Удаление фотографий*/
        if (dataManager.getAdmin().isDeletePhotos()) {
            dataManager.removeImgFromPortfolio(fileUniqueId);
            return new SimpleSendMessage("Фотография удалена (если она была в базе)",0).getNewMessage(update);
        }


        /*Добавление фотографий*/
        if (!dataManager.getAdmin().getViewingType().equals("-none-")) {
            if (dataManager.addPortfolioImg(fileUniqueId, fileId)) {
                return new SimpleSendMessage("Фотография загружена", 0).getNewMessage(update);
            } else {
                return new SimpleSendMessage("Такая фотография уже присутсвует в базе", 0).getNewMessage(update);
            }
        }

        return new SimpleSendMessage("Для загрузки/ удаления фотографий необходимо выбрать соответсвующий раздел портфолио",0 ).getNewMessage(update);
    }



}
