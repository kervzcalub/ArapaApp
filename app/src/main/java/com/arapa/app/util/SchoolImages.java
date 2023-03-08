package com.arapa.app.util;

import java.util.ArrayList;
import java.util.List;

public class SchoolImages {

    List<String> image_paths;

    public List<String> getImage_paths() {
        return image_paths;
    }

    public SchoolImages() {
        image_paths = new ArrayList<>();
    }

    public void addImagePath(String path) {
        image_paths.add(path);
    }

    public String getImagePath(int pos) {
        return image_paths.get(pos);
    }

    public int getMaxIndex() {
        return image_paths.size();
    }
}
