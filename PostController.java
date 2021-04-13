package main.java.com.minaev.crud.controller;

import main.java.com.minaev.crud.model.Label;
import main.java.com.minaev.crud.model.Post;
import main.java.com.minaev.crud.repository.PostRepository;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PostController {
    private PostRepository postRepository = new PostRepository();
    private Post post = new Post();
    private LabelController labelController = new LabelController();



    public Post newPost(String content, String labelIds) {
        post.setId(postRepository.getNewId());
        post.setContent(content);
        post.setCreated(setCurrentTime());
        post.setUpdated(setCurrentTime());
        post.setLabels(getListLabelsByString(labelIds));
        postRepository.savePost(post);
        return post;
    }

    public Post changeContent(int id, String content) {
        post = postRepository.getById(id);
        post.setContent(content);
        post.setUpdated(setCurrentTime());
        postRepository.update(post);
        return post;
    }

    public Post changeTags(int id, String labelsId) {
        post = postRepository.getById(id);
        post.setUpdated(setCurrentTime());
        post.setLabels(getListLabelsByString(labelsId));
        postRepository.update(post);
        return post;
    }

    public void delPost(int id) {
        postRepository.deleteById(id);
    }

    public List<Post> getAllPost() {
        return postRepository.getAll();
    }

    public Post getById(int id) {
        return postRepository.getById(id);
    }

    private List<Label> getListLabelsByString(String labelIds) {
        String[] labelIdsAr = labelIds.split(",");
        try {
            return Arrays.stream(labelIdsAr).map(strId -> {
                return Integer.parseInt(strId);
            }).map(id -> {
                return labelController.getById(id);
            }).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            System.out.println("Не удалось преобразовать строку в число в методе getListLabelsByString :" + e);
            return Collections.emptyList();
        }
    }

    private long setCurrentTime() {
        Date now = new Date();
        return now.getTime();
    }


}
