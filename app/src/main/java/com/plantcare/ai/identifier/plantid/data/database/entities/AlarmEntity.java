package com.plantcare.ai.identifier.plantid.data.database.entities;

import static com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_DEFAULT;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import androidx.annotation.IntDef;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Entity(tableName = "Record_Alarm")
public final class AlarmEntity implements Parcelable, Serializable {

    public static final int TYPE_ALARM_NORMAL = 0;
    public static final int TYPE_ALARM_SNOOZE = 1;

    private AlarmEntity(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        label = in.readString();
        description = in.readString();
        allDays = in.readSparseBooleanArray();
        soundRes = in.readInt();
        isEnabled = in.readByte() != 0;
        isVibration = in.readByte() != 0;
        type = in.readInt();
    }

    public static final Creator<AlarmEntity> CREATOR = new Creator<AlarmEntity>() {
        @Override
        public AlarmEntity createFromParcel(Parcel in) {
            return new AlarmEntity(in);
        }

        @Override
        public AlarmEntity[] newArray(int size) {
            return new AlarmEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(time);
        parcel.writeString(label);
        parcel.writeSparseBooleanArray(allDays);
        parcel.writeInt(soundRes);
        parcel.writeByte((byte) (isEnabled ? 1 : 0));
        parcel.writeByte((byte) (isVibration ? 1 : 0));
        parcel.writeInt(type);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON, TUES, WED, THURS, FRI, SAT, SUN})
    @interface Days {
    }

    public static final int MON = 1;
    public static final int TUES = 2;
    public static final int WED = 3;
    public static final int THURS = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
    public static final int SUN = 7;

    private static final long NO_ID = -1;

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long time;
    private String label;
    private String description;
    private SparseBooleanArray allDays;
    private int soundRes;
    private boolean isEnabled;
    private boolean isVibration;
    private int type;

    public void setAllDays(SparseBooleanArray allDays) {
        this.allDays = allDays;
    }

    public SparseBooleanArray getAllDays() {
        return allDays;
    }

    public AlarmEntity() {
        this(NO_ID, TYPE_ALARM_NORMAL);
    }

    public AlarmEntity(long id, int type) {
        this(id, System.currentTimeMillis(), type);
    }

    public AlarmEntity(long id, long time, int type,@Days int... days) {
        this(id, time, null, null, SOUND_DEFAULT, type,days);
    }

    public AlarmEntity(long id, long time, String label, String description, int soundRes, int type,@Days int... days) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.description = description;
        this.soundRes = soundRes;
        this.allDays = buildDaysArray(days);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String des) {
        this.description = des;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLabel() {
        return label;
    }

    public void setDay(@Days int day, boolean isAlarmed) {
        allDays.append(day, isAlarmed);
    }

    public SparseBooleanArray getDays() {
        return allDays;
    }

    public boolean getDay(@Days int day) {
        return allDays.get(day);
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setIsVibration(boolean is) {
        this.isVibration = is;
    }

    public boolean isVibration() {
        return this.isVibration;
    }

    public void setSoundRes(int sound) {
        this.soundRes = sound;
    }

    public int getSoundRes() {
        return this.soundRes;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int notificationId() {
        final long id = getId();
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Alarm{" + "id=" + id + ", time=" + time + ", label='" + label + '\'' + ", allDays=" + allDays + ", isEnabled=" + isEnabled + ", isVibration=" + isVibration + ", Sound Id: " + soundRes + ", Des: " + description + '}';
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + label.hashCode();
        for (int i = 0; i < allDays.size(); i++) {
            result = 31 * result + (allDays.valueAt(i) ? 1 : 0);
        }
        return result;
    }

    private static SparseBooleanArray buildDaysArray(@Days int... days) {

        final SparseBooleanArray array = buildBaseDaysArray();

        for (@Days int day : days) {
            array.append(day, true);
        }

        return array;

    }

    private static SparseBooleanArray buildBaseDaysArray() {

        final int numDays = 7;

        final SparseBooleanArray array = new SparseBooleanArray(numDays);

        array.put(MON, false);
        array.put(TUES, false);
        array.put(WED, false);
        array.put(THURS, false);
        array.put(FRI, false);
        array.put(SAT, false);
        array.put(SUN, false);

        return array;

    }

}
