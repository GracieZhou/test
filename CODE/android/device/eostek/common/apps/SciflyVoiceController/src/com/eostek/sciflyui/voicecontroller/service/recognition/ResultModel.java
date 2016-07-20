
package com.eostek.sciflyui.voicecontroller.service.recognition;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youpeng.wan
 */
public class ResultModel implements Parcelable {
    private String mMeaning = "hello youpeng.";

    private int mConfidenceScore = 10;

    public ResultModel() {
    }

    public ResultModel(String mMeaning, int mConfidenceScore) {
        this.mMeaning = mMeaning;
        this.mConfidenceScore = mConfidenceScore;
    }

    /**
     * meaning of result.
     * 
     * @return meaning.
     */
    public String getmMeaning() {
        return mMeaning;
    }

    /**
     * set meaning.
     * 
     * @param meaning meaning.
     */
    public void setmMeaning(String meaning) {
        this.mMeaning = meaning;
    }

    /**
     * get confidence score.
     * 
     * @return confidence score.
     */
    public int getConfidenceScore() {
        return mConfidenceScore;
    }

    /**
     * set confidence score.
     * 
     * @param confidenceScore conficence score.
     */
    public void setConfidenceScore(int confidenceScore) {
        this.mConfidenceScore = confidenceScore;
    }

    /**
     * toString.
     * 
     * @return string.
     */
    @Override
    public String toString() {
        return "RecResultModel [mMeaning=" + mMeaning + ", confidenceScore=" + mConfidenceScore + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMeaning);
        dest.writeInt(mConfidenceScore);
    }

    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {

        @Override
        public ResultModel createFromParcel(Parcel source) {
            String meaning = source.readString();
            int confidence = source.readInt();
            return new ResultModel(meaning, confidence);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }

    };

}
