package main.java.com.minaev.crud.controller;

import main.java.com.minaev.crud.model.Label;
import main.java.com.minaev.crud.repository.LabelRepository;
import main.java.com.minaev.crud.view.LabelView;

import java.util.List;
import java.util.stream.Collectors;

public class LabelController {
    private  LabelRepository labelRepository = new LabelRepository();

    public Label newLabel(String category) {
        return labelRepository.saveLabel(new Label(labelRepository.getNewId(), category));
        }


    public Label updateLabel(int id, String label) {
       return labelRepository.update(new Label(id, label));
    }


    public void deleteLabel(int id) {
       labelRepository.deleteById(id);
    }

    public Label getById(int id){
        return labelRepository.getById(id);
    }


    public List<String> getAll() {

        return labelRepository.getAll().stream().
                map(label -> label.toString()).
                collect(Collectors.toList());
    }

}
