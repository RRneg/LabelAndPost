package main.java.com.minaev.crud.repository;

import main.java.com.minaev.crud.model.Label;
import main.java.com.minaev.crud.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PostRepository {
    private final Path path = Paths.get("src/main/resources/files/Post.txt");
    private final LabelRepository labelRepository = new LabelRepository();
    private final String dateFormat = "dd.MM.yyyy HH:mm:ss:SSS";

    public PostRepository() {
    }

    public Integer getNewId() {
        return getAllInternal().stream().
                max((a, b) -> Integer.compare(a.getId(), b.getId())).
                orElse(new Post()).getId() + 1;
    }

    private long getCurrentData() {
        Date now = new Date();
        return now.getTime();
    }

    private String longTimeToString(long time) {
        Date date = new Date(time);
        return new SimpleDateFormat(dateFormat).format(date);
    }

    private long stringTimeToLong(String strTime) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(dateFormat);
        try {
            Date date = format.parse(strTime);
            return date.getTime();
        } catch (ParseException e) {
            System.out.println("Не удалось пеобразовать строку в дату  " + e);
            return 0;
        }

    }

    private void createNewFileNIO() {
        try {
            if (!Files.exists(path))
                Files.createFile(path);
        } catch (IOException e) {
            System.out.println("Не удалось создать файл-репозиторий Post:" + e);
        }
    }

    public Post getById(int id) {
        return getAllInternal().stream().filter(post -> post.getId() == id).findFirst().orElse(null);
    }

    private List<Post> getAllInternal() {
        try {
            return Files.readAllLines(path).stream().filter(str -> str != "").map(str -> {
                return stringToPost(str);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл" + e);
            return Collections.emptyList();
        }
    }

    public List<Post> getAll() {
        return getAllInternal();
    }


    public Post update(Post post) {
        reSaveListPost(getAllInternal().stream().peek(post1 -> {
            if (post1.getId() == post.getId()) {
                post1.setContent(post.getContent());
                post1.setUpdated(getCurrentData());
                post1.setLabels(post.getLabels());
            }
        }).collect(Collectors.toList()));
        return post;
    }

    private void reSaveListPost(List<Post> posts) {
        try {
            Files.delete(path);
            createNewFileNIO();
        } catch (IOException e) {
            System.out.println("Не удалось обновить файл :" + e);
        }
        posts.stream().distinct().sorted((a, b) -> Integer.compare(a.getId(), b.getId())).
                collect(Collectors.toList()).stream().
                forEach(a -> savePost(a));
    }

    public void deleteById(int id) {
        List<Post> posts = getAllInternal();
        posts.removeIf(post -> post.getId() == id);
        reSaveListPost(posts);
    }

    public Post savePost(Post post) {
        String post1 = postToString(post) + System.getProperty("line.separator");
        try {
            Files.writeString(path, post1, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Не удалось записать в файл: " + e);
        }
        return post;
    }

    private String postToString(Post post) {
        return post.getId() + ";" + post.getContent() + ";"
                + longTimeToString(post.getCreated()) + ";"
                + longTimeToString(post.getUpdated()) + ";"
                + listIdToString(post.getLabels());
    }

    private Post stringToPost(String str) {
        Post post = new Post();
        String[] partsPost = str.split(";");
        try {
            post.setId(Integer.parseInt(partsPost[0]));
        } catch (NumberFormatException e) {
            System.out.println("Не можем преобразовать строку в число в stringToPost  " + e);
        }
        post.setContent(partsPost[1]);
        post.setCreated(stringTimeToLong(partsPost[2]));
        post.setUpdated(stringTimeToLong(partsPost[3]));
        post.setLabels(getListLabelsByIds(partsPost[4]));

        return post;
    }

    private List<Label> getListLabelsByIds(String str) {
        str = str.replaceAll("/", "");
        str = str.replaceAll(" ", "");
        String[] stringId = str.split(",");
        try {
            return Arrays.stream(stringId).map(strId -> {
                return Integer.parseInt(strId);
            }).collect(Collectors.toList()).
                    stream().map(id -> {
                return labelRepository.getById((int) id);
            }).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            System.out.println("Не можем преобразовать строку в число в методе getListLabelsByIds  " + e);
            return Collections.emptyList();
        }
    }

    private String listIdToString(List<Label> labels) {
        String listToStr = labels.stream().map(label -> {
            return label.getId();
        }).map(id -> {
            return String.valueOf(id) + " ,";
        }).reduce((strResult, str) ->
                strResult + str).orElse(null);
        return "/" + listToStr.substring(0, listToStr.length() - 1) + "/";
    }

}
