package io.mazur.fit.stream;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

import io.mazur.fit.App;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.SectionMode;
import io.mazur.fit.model.realm.RealmCategory;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.model.realm.RealmSection;
import io.mazur.fit.model.realm.RealmSet;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import rx.Observable;
import rx.subjects.PublishSubject;

public class RealmStream {
    private final PublishSubject<RealmRoutine> mRealmRoutineSubject = PublishSubject.create();

    private static RealmStream sRealmStream = null;

    public static RealmStream getInstance() {
        if(sRealmStream == null) {
            sRealmStream = new RealmStream();
        }

        return sRealmStream;
    }

    private RealmStream() {}

    public Realm getRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder(App.getContext())
                .name("bodyweight.fitness.realm")
                // TODO: Delete this in production.
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build());
    }

    public RealmRoutine buildRealmRoutine(Routine routine) {
        getRealm().beginTransaction();

        RealmRoutine realmRoutine = getRealm().createObject(RealmRoutine.class);
        realmRoutine.setId("Routine-" + UUID.randomUUID().toString());
        realmRoutine.setStartTime(new DateTime().toDate());
        realmRoutine.setLastUpdatedTime(new DateTime().toDate());

        RealmCategory realmCategory = null;
        RealmSection realmSection = null;

        for(Exercise exercise : routine.getExercises()) {
            RealmExercise realmExercise = getRealm().createObject(RealmExercise.class);
            realmExercise.setId("Exercise-" + UUID.randomUUID().toString());
            realmExercise.setTitle(exercise.getTitle());
            realmExercise.setDescription(exercise.getDescription());
            realmExercise.setDefaultSet(exercise.getDefaultSet());

            RealmSet realmSet = getRealm().createObject(RealmSet.class);
            realmSet.setId("Set-" + UUID.randomUUID().toString());

            if (exercise.getDefaultSet().equals("weighted")) {
                realmSet.setIsTimed(false);
            } else {
                realmSet.setIsTimed(true);
            }

            realmSet.setSeconds(0);
            realmSet.setWeight(0);
            realmSet.setReps(0);

            realmExercise.getSets().add(realmSet);

            if(realmCategory == null || !realmCategory.getTitle().equalsIgnoreCase(exercise.getCategory().getTitle())) {
                realmCategory = getRealm().createObject(RealmCategory.class);
                realmCategory.setId("Category-" + UUID.randomUUID().toString());
                realmCategory.setTitle(exercise.getCategory().getTitle());

                realmRoutine.getCategories().add(realmCategory);
            }

            if(realmSection == null || !realmSection.getTitle().equalsIgnoreCase(exercise.getSection().getTitle())) {
                realmSection = getRealm().createObject(RealmSection.class);
                realmSection.setId("Section-" + UUID.randomUUID().toString());
                realmSection.setTitle(exercise.getSection().getTitle());
                realmSection.setMode(exercise.getSection().getSectionMode().toString());
                realmSection.setRealmCategory(realmCategory);

                realmCategory.getSections().add(realmSection);
            }

            realmExercise.setCategory(realmCategory);
            realmExercise.setSection(realmSection);

            /**
             * Hide exercises not relevant to user level.
             */
            if(exercise.getSection().getSectionMode().equals(SectionMode.LEVELS) ||
                    exercise.getSection().getSectionMode().equals(SectionMode.PICK)) {
                if(exercise.equals(exercise.getSection().getCurrentExercise())) {
                    realmExercise.setVisible(true);
                } else {
                    realmExercise.setVisible(false);
                }
            } else {
                realmExercise.setVisible(true);
            }

            realmRoutine.getExercises().add(realmExercise);
            realmCategory.getExercises().add(realmExercise);
            realmSection.getExercises().add(realmExercise);
        }

        getRealm().commitTransaction();

        return realmRoutine;
    }

    public RealmRoutine getRealmRoutineForToday() {
        final Date start = new DateTime()
                .withTimeAtStartOfDay()
                .toDate();

        final Date end = new DateTime()
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toDate();

        RealmRoutine realmRoutine = getRealm()
                .where(RealmRoutine.class)
                .between("startTime", start, end)
                .findFirst();

        if(realmRoutine == null) {
            realmRoutine = buildRealmRoutine(
                    RoutineStream.getInstance().getRoutine()
            );

            mRealmRoutineSubject.onNext(realmRoutine);
        }

        return realmRoutine;
    }

    public Observable<RealmRoutine> getRealmRoutineObservable() {
        return mRealmRoutineSubject;
    }
}
