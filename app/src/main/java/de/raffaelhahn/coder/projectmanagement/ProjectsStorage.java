package de.raffaelhahn.coder.projectmanagement;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.raffaelhahn.coder.utils.LocalDateTimeAdapter;

public class ProjectsStorage {

    public static List<Project> getProjects(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("projects", Context.MODE_PRIVATE);
        String projects = sharedPreferences.getString("projects", null);
        if (projects != null) {
            return getGson().fromJson(projects, new TypeToken<List<Project>>(){}.getType());
        }
        return new ArrayList<>();
    }

    public static void addProject(Context context, Project project) {
        List<Project> projects = getProjects(context);
        projects.removeIf(p -> p.getPath().equals(project.getPath()));
        projects.add(project);
        saveProjects(context, projects);
    }

    public static void saveProjects(Context context, List<Project> projects) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("projects", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("projects", getGson().toJson(projects));
        editor.apply();
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe()).create();
    }

}
