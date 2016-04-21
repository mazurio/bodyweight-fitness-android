package com.bodyweight.fitness.stream;

import com.bodyweight.fitness.App;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.model.SectionMode;
import com.bodyweight.fitness.model.repository.RepositoryCategory;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.model.repository.RepositorySection;
import com.bodyweight.fitness.model.repository.RepositorySet;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RepositoryStream {
    private static RepositoryStream sRepositoryStream = null;

    public static RepositoryStream getInstance() {
        if(sRepositoryStream == null) {
            sRepositoryStream = new RepositoryStream();
        }

        return sRepositoryStream;
    }

    private RepositoryStream() {}

    public Realm getRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder(App.getContext())
                .name("bodyweight.fitness.realm")
                .schemaVersion(2)
                .migration((DynamicRealm realm, long oldVersion, long newVersion) -> {
                    RealmSchema schema = realm.getSchema();
                    RealmObjectSchema routineSchema = schema.get("RepositoryRoutine");

                    routineSchema
                            .addField("title", String.class)
                            .addField("subtitle", String.class)
                            .transform((DynamicRealmObject obj) -> {
                                obj.set("title", "Bodyweight Fitness");
                                obj.set("subtitle", "Recommended Routine");
                            });
                })
                .build());
    }

    public RepositoryRoutine buildRealmRoutine(Routine routine) {
        Realm realm = getRealm();

        realm.beginTransaction();

        RepositoryRoutine repositoryRoutine = realm.createObject(RepositoryRoutine.class);
        repositoryRoutine.setId("Routine-" + UUID.randomUUID().toString());
        repositoryRoutine.setRoutineId(routine.getRoutineId());
        repositoryRoutine.setTitle(routine.getTitle());
        repositoryRoutine.setSubtitle(routine.getSubtitle());
        repositoryRoutine.setStartTime(new DateTime().toDate());
        repositoryRoutine.setLastUpdatedTime(new DateTime().toDate());

        RepositoryCategory repositoryCategory = null;
        RepositorySection repositorySection = null;

        for(Exercise exercise : routine.getExercises()) {
            RepositoryExercise repositoryExercise = realm.createObject(RepositoryExercise.class);
            repositoryExercise.setId("Exercise-" + UUID.randomUUID().toString());
            repositoryExercise.setExerciseId(exercise.getExerciseId());
            repositoryExercise.setTitle(exercise.getTitle());
            repositoryExercise.setDescription(exercise.getDescription());
            repositoryExercise.setDefaultSet(exercise.getDefaultSet());

            RepositorySet repositorySet = realm.createObject(RepositorySet.class);
            repositorySet.setId("Set-" + UUID.randomUUID().toString());

            if (exercise.getDefaultSet().equals("weighted")) {
                repositorySet.setIsTimed(false);
            } else {
                repositorySet.setIsTimed(true);
            }

            repositorySet.setSeconds(0);
            repositorySet.setWeight(0);
            repositorySet.setReps(0);
            repositorySet.setExercise(repositoryExercise);

            repositoryExercise.getSets().add(repositorySet);

            if(repositoryCategory == null || !repositoryCategory.getTitle().equalsIgnoreCase(exercise.getCategory().getTitle())) {
                repositoryCategory = realm.createObject(RepositoryCategory.class);
                repositoryCategory.setId("Category-" + UUID.randomUUID().toString());
                repositoryCategory.setCategoryId(exercise.getCategory().getCategoryId());
                repositoryCategory.setTitle(exercise.getCategory().getTitle());
                repositoryCategory.setRoutine(repositoryRoutine);

                repositoryRoutine.getCategories().add(repositoryCategory);
            }

            if(repositorySection == null || !repositorySection.getTitle().equalsIgnoreCase(exercise.getSection().getTitle())) {
                repositorySection = realm.createObject(RepositorySection.class);
                repositorySection.setId("Section-" + UUID.randomUUID().toString());
                repositorySection.setSectionId(exercise.getSection().getSectionId());
                repositorySection.setTitle(exercise.getSection().getTitle());
                repositorySection.setMode(exercise.getSection().getSectionMode().toString());
                repositorySection.setRoutine(repositoryRoutine);
                repositorySection.setCategory(repositoryCategory);

                repositoryRoutine.getSections().add(repositorySection);
                repositoryCategory.getSections().add(repositorySection);
            }

            repositoryExercise.setRoutine(repositoryRoutine);
            repositoryExercise.setCategory(repositoryCategory);
            repositoryExercise.setSection(repositorySection);

            /**
             * Hide exercises not relevant to user level.
             */
            if(exercise.getSection().getSectionMode().equals(SectionMode.LEVELS) || exercise.getSection().getSectionMode().equals(SectionMode.PICK)) {
                if(exercise.equals(exercise.getSection().getCurrentExercise())) {
                    repositoryExercise.setVisible(true);
                } else {
                    repositoryExercise.setVisible(false);
                }
            } else {
                repositoryExercise.setVisible(true);
            }

            repositoryRoutine.getExercises().add(repositoryExercise);
            repositoryCategory.getExercises().add(repositoryExercise);
            repositorySection.getExercises().add(repositoryExercise);
        }

        realm.commitTransaction();

        return repositoryRoutine;
    }

    public RepositoryRoutine getRepositoryRoutineForToday() {
        final Date start = new DateTime()
                .withTimeAtStartOfDay()
                .toDate();

        final Date end = new DateTime()
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toDate();

        String routineId = RoutineStream.getInstance().getRoutine().getRoutineId();

        RepositoryRoutine repositoryRoutine = getRealm()
                .where(RepositoryRoutine.class)
                .between("startTime", start, end)
                .equalTo("routineId", routineId)
                .findFirst();

        if (repositoryRoutine == null) {
            repositoryRoutine = buildRealmRoutine(
                    RoutineStream.getInstance().getRoutine()
            );
        }

        return repositoryRoutine;
    }

    public boolean repositoryRoutineForTodayExists() {
        final Date start = new DateTime()
                .withTimeAtStartOfDay()
                .toDate();

        final Date end = new DateTime()
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toDate();

        String routineId = RoutineStream.getInstance().getRoutine().getRoutineId();

        RepositoryRoutine repositoryRoutine = getRealm()
                .where(RepositoryRoutine.class)
                .between("startTime", start, end)
                .equalTo("routineId", routineId)
                .findFirst();

        if (repositoryRoutine == null) {
            return false;
        }

        return true;
    }
}
