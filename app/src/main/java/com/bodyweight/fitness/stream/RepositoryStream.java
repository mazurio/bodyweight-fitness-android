package com.bodyweight.fitness.stream;

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.model.Exercise;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.model.SectionMode;
import com.bodyweight.fitness.model.repository.RepositoryCategory;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.model.repository.RepositorySection;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.utils.Logger;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import rx.Observable;
import rx.subjects.PublishSubject;

public class RepositoryStream {
    private final PublishSubject<RepositoryRoutine> mSubject = PublishSubject.create();

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
                    Logger.d("old= " + oldVersion + " new=" + newVersion);

                    RealmSchema schema = realm.getSchema();

//                    if (oldVersion == 0) {
                        RealmObjectSchema routineSchema = schema.get("RepositoryRoutine");

                        routineSchema
                                .addField("title", String.class)
                                .addField("subtitle", String.class)
                                .transform((DynamicRealmObject obj) -> {
                                    obj.set("title", "Bodyweight Fitness");
                                    obj.set("subtitle", "Beginner Routine");
                                });
//                    }
                })
                .build());
    }

    public RepositoryRoutine buildRealmRoutine(Routine routine) {
        getRealm().beginTransaction();

        RepositoryRoutine repositoryRoutine = getRealm().createObject(RepositoryRoutine.class);
        repositoryRoutine.setId("Routine-" + UUID.randomUUID().toString());
        repositoryRoutine.setRoutineId(routine.getRoutineId());
        repositoryRoutine.setTitle(routine.getTitle());
        repositoryRoutine.setSubtitle(routine.getSubtitle());
        repositoryRoutine.setStartTime(new DateTime().toDate());
        repositoryRoutine.setLastUpdatedTime(new DateTime().toDate());

        RepositoryCategory repositoryCategory = null;
        RepositorySection repositorySection = null;

        for(Exercise exercise : routine.getExercises()) {
            RepositoryExercise repositoryExercise = getRealm().createObject(RepositoryExercise.class);
            repositoryExercise.setId("Exercise-" + UUID.randomUUID().toString());
            repositoryExercise.setExerciseId(exercise.getExerciseId());
            repositoryExercise.setTitle(exercise.getTitle());
            repositoryExercise.setDescription(exercise.getDescription());
            repositoryExercise.setDefaultSet(exercise.getDefaultSet());

            RepositorySet repositorySet = getRealm().createObject(RepositorySet.class);
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
                repositoryCategory = getRealm().createObject(RepositoryCategory.class);
                repositoryCategory.setId("Category-" + UUID.randomUUID().toString());
                repositoryCategory.setCategoryId(exercise.getCategory().getCategoryId());
                repositoryCategory.setTitle(exercise.getCategory().getTitle());
                repositoryCategory.setRoutine(repositoryRoutine);

                repositoryRoutine.getCategories().add(repositoryCategory);
            }

            if(repositorySection == null || !repositorySection.getTitle().equalsIgnoreCase(exercise.getSection().getTitle())) {
                repositorySection = getRealm().createObject(RepositorySection.class);
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
            if(exercise.getSection().getSectionMode().equals(SectionMode.LEVELS) ||
                    exercise.getSection().getSectionMode().equals(SectionMode.PICK)) {
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

        getRealm().commitTransaction();

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

        if(repositoryRoutine == null) {
            repositoryRoutine = buildRealmRoutine(
                    RoutineStream.getInstance().getRoutine()
            );

            mSubject.onNext(repositoryRoutine);
        }

        return repositoryRoutine;
    }

    public Observable<RepositoryRoutine> getRepositoryRoutineObservable() {
        return mSubject;
    }
}
