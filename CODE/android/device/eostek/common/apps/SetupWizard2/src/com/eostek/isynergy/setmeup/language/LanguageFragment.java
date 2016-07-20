
package com.eostek.isynergy.setmeup.language;

import java.util.List;

import android.R.integer;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.internal.app.LocalePicker;
import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.WizardHolder;
import com.eostek.isynergy.setmeup.model.LanguageModel;
import com.eostek.isynergy.setmeup.utils.Utils;

public class LanguageFragment extends Fragment {

    protected static final String TAG = LanguageFragment.class.getSimpleName();

    private ListView mLanguageLv;

    private int mPosition;
    
    private final int LANGUAGE_CN = 0;
    
    private final int LANGUAGE_FR = 1;
    
    private final int LANGUAGE_EN = 2;
    
    private final int LANGUAGE_TW = 3;

    private LanguageLogic mLanguageLogic;

    List<LanguageModel> mLanguages;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_language, container, false);
        this.mLanguageLv = (ListView) v.findViewById(R.id.lv_language);

        this.mLanguageLogic = new LanguageLogic(getActivity());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        WizardHolder.leftBtn.setVisibility(View.INVISIBLE);
        mLanguages = mLanguageLogic.getAllLanguages();
        LanguageAdapter myAdapter = new LanguageAdapter(getActivity(), mLanguages);
        mLanguageLv.setAdapter(myAdapter);
        mLanguageLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Utils.print(TAG, "position:" + position);
                LocalePicker.updateLocale(mLanguages.get(position).getLocale());
            }
        });

        mLanguageLv.requestFocus();
        
        setDefaultIME(getLanguagePos(mLanguages));
        mLanguageLv.setSelection(getLanguagePos(mLanguages));
    }

    private void setDefaultIME(int languagePos) {
		switch (languagePos) {
		case LANGUAGE_CN:
			Settings.Secure.putString(this.getActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,
		               "com.eostek.scifly.ime/.pinyin.PinyinImeService");
			Log.d(TAG, "LANGUAGE_FR --> default ime is PinyinImeService");
			break;
		case LANGUAGE_FR:
			Settings.Secure.putString(this.getActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,
		               "com.eostek.scifly.ime/.french.FrenchImeService");
			Log.d(TAG, "LANGUAGE_FR --> default ime is FrenchImeService");
			break;
		case LANGUAGE_EN:
			Settings.Secure.putString(this.getActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,
		               "com.eostek.scifly.ime/.zhuyin.ZhuyinImeService");
			Log.d(TAG, "LANGUAGE_EN --> default ime is ZhuyinImeService");
			break;
		case LANGUAGE_TW:
			Settings.Secure.putString(this.getActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,
		               "com.eostek.scifly.ime/.zhuyin.ZhuyinImeService");
			Log.d(TAG, "LANGUAGE_TW --> default ime is ZhuyinImeService");
			break;

		default:
			break;
		}
		
	}

	private int getLanguagePos(List<LanguageModel> lans) {
        String currentLanguage = getActivity().getResources().getConfiguration().locale + "";
        for (LanguageModel lm: lans) {
            if (currentLanguage.equals(lm.getOrigalLocale())) {
                return lans.indexOf(lm);
            }
        }
        return 0;
    }

}
