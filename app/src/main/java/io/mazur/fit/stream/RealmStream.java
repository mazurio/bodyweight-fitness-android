package io.mazur.fit.stream;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

import io.mazur.fit.App;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.model.realm.RealmSet;
import io.mazur.fit.utils.Logger;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmStream {
    private static RealmStream sRealmStream = null;

    public static RealmStream getInstance() {
        if(sRealmStream == null) {
            sRealmStream = new RealmStream();
        }

        return sRealmStream;
    }

    public RealmStream() {
        super();
    }

    public Realm getRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder(App.getContext())
                .name("20.realm")
                .schemaVersion(1)
                .build());
    }

    public RealmRoutine buildRealmRoutineFrom(Routine routine) {
        Logger.d("Building Realm Routine");

        getRealm().beginTransaction();

        RealmRoutine realmRoutine = getRealm().createObject(RealmRoutine.class);
        realmRoutine.setId("Routine-" + UUID.randomUUID().toString());
        realmRoutine.setDate(new DateTime().toDate());

        for(Exercise exercise : routine.getExercises()) {
            RealmExercise realmExercise = getRealm().createObject(RealmExercise.class);
            realmExercise.setId("Exercise-" + UUID.randomUUID().toString());
            realmExercise.setTitle(exercise.getTitle());
            realmExercise.setDescription(exercise.getDescription());

            RealmSet realmSet = getRealm().createObject(RealmSet.class);
            realmSet.setId("Set-" + UUID.randomUUID().toString());

            if(exercise.allowTimeReps()) {
                realmSet.setType(RealmSet.TIME);
            } else {
                realmSet.setType(RealmSet.BODYWEIGHT);
            }

            realmSet.setValue(0);

            realmExercise.getSets().add(realmSet);

            realmRoutine.getExercises().add(realmExercise);
        }

        getRealm().commitTransaction();

        return realmRoutine;
    }

    public RealmRoutine buildRealmRoutineFrom(Routine routine, DateTime dateTime) {
        Logger.d("Building Realm Routine");

        getRealm().beginTransaction();

        RealmRoutine realmRoutine = getRealm().createObject(RealmRoutine.class);
        realmRoutine.setId("Routine-" + UUID.randomUUID().toString());
        realmRoutine.setDate(dateTime.toDate());

        for(Exercise exercise : routine.getExercises()) {
            RealmExercise realmExercise = getRealm().createObject(RealmExercise.class);
            realmExercise.setId("Exercise-" + UUID.randomUUID().toString());
            realmExercise.setTitle(exercise.getTitle());
            realmExercise.setDescription(exercise.getDescription());

            RealmSet realmSet = getRealm().createObject(RealmSet.class);
            realmSet.setId("Set-" + UUID.randomUUID().toString());

            if(exercise.allowTimeReps()) {
                realmSet.setType(RealmSet.TIME);
            } else {
                realmSet.setType(RealmSet.BODYWEIGHT);
            }

            realmSet.setValue(0);

            realmExercise.getSets().add(realmSet);

            realmRoutine.getExercises().add(realmExercise);
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
                .minusMinutes(1)
                .toDate();

        RealmRoutine realmRoutine = getRealm()
                .where(RealmRoutine.class)
                .between("date", start, end)
                .findFirst();

        if(realmRoutine == null) {
            realmRoutine = RealmStream.getInstance().buildRealmRoutineFrom(RoutineStream.getInstance().getRoutine());
        }

        return realmRoutine;
    }
}
