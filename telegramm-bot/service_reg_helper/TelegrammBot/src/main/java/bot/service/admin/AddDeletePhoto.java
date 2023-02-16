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
import java.util.Objects;

public class AddDeletePhoto extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        if (!Objects.equals(dataManager.ADMIN_ID, update.getMessage().getChatId())){
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

        /*Удаление фотографий*/
        if (dataManager.admin_deletePhotos) {
            if (dataManager.removeImgFromPortfolio(fileUniqueId)) {
                return new SimpleSendMessage("Фотография удалена", 0).getNewMessage(update);
            } else {
                return new SimpleSendMessage("К сожалению, данная фотография в базе не найдена. \n\n Для удаления фотографий из портфолио надо:\n" +
                        "1) Выбрать фотографии в галерее телефона или компьютера\n" +
                        "2) Прислать в телеграмм\n" +
                        "Использовать фотографии для удаления из галереи 'портфолио' самого же бота не допускается. Эти фото изменены (размер, сжатие), и не будут опознаны", 0).getNewMessage(update);
            }
        }


        /*Добавление фотографий*/
        if (!dataManager.getUserViewType(dataManager.ADMIN_ID).equals("-none-")) {
            if (dataManager.addPortfolioImg(fileUniqueId, fileId)) {
                return new SimpleSendMessage("Фотография загружена", 0).getNewMessage(update);
            } else {
                return new SimpleSendMessage("Такая фотография уже присутсвует в базе", 0).getNewMessage(update);
            }
        }

        return new SimpleSendMessage("Для загрузки/ удаления фотографий необходимо выбрать соответсвующий раздел портфолио",0 ).getNewMessage(update);
    }



}
