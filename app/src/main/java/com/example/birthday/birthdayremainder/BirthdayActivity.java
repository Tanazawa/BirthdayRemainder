package com.example.birthday.birthdayremainder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import static android.view.View.OnClickListener;


public class BirthdayActivity extends Activity {

    private int year;
    private int month;
    private int day;
    private int age;

    private TextView dispView;

    private TextView ageView;

    // デフォルトメッセージ
    private static final String DEFAULT_MSG = "誕生日を設定してください";

    // プレファレンスファイル名
    private static final String PREFS_FILE = "MyPrefsFile";
    // プレファレンスのためのキー
    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    private static final String DAY = "DAY";

    // 「クリア」ボタンのイベントリスナー
    public class ClearButtonClickListener implements OnClickListener{
        public void onClick(View v) {
            // プレファレンスをクリア
            SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            dispView.setText(DEFAULT_MSG);
            ageView.setText("");
            year = month = day = 0;
        }
    }

    // 「誕生日設定」ボタンのイベントリスナー
    public class SetButtonClickListener implements OnClickListener {
        public void onClick(View v) {
            // DatePickerDaialogを表示
            new DatePickerDialog(BirthdayActivity.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    BirthdayActivity.this.year = year;
                    month = monthOfYear;
                    day = dayOfMonth;
                    showResult();

                    savePrefs();
                }
            }, 1979, 6, 3).show();
        };
    }

    // 結果を表示する
    private void showResult() {
        String dStr = String.format("%04d/%02d/%02d", year, month + 1, day);
        dispView.setText(dStr);

        // 現在の日時を表すCalenderオブジェクトを生成
        Calendar now = Calendar.getInstance();

        // 誕生日を管理するCalenderオブジェクトを生成
        Calendar birthday = (Calendar) now.clone();
        birthday.set(year, month, day);
        // 年齢を求める
        age = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR) - 1;
        // birthdayを今年の誕生日に設定
        int thisYear = now.get(Calendar.YEAR);
        birthday.set(Calendar.YEAR, thisYear);

        if (now.after(birthday)){
            // 誕生日が過ぎていれば来年に
            birthday.add(Calendar.YEAR, 1);
            // 年齢を増やす
            age += 1;
        } else if(now.equals(birthday)) {
            // 今日が誕生日であれば年齢を1増やす
            age += 1;
        }

        // 今年の誕生日までの日数を求める
        int diff = (int) ((birthday.getTimeInMillis() - now.getTimeInMillis()) /
                (1000 * 60 * 60 * 24));
        String dispStr = "誕生日まであと" + Integer.toString(diff) + "日";
        // 日数を表示する
        dispView.setText(dispStr);

        // 年齢を表示する
        ageView.setText(age + "才");
    }

    // プリファレンスに保存
    private void savePrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(YEAR, year);
        editor.putInt(MONTH, month);
        editor.putInt(DAY, day);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);

        // 誕生日設定ボタンのイベントリスナーを設定
        Button setDateBtn = (Button) findViewById(R.id.setDateBtn);
        setDateBtn.setOnClickListener(new SetButtonClickListener());

        // クリアボタンのイベントリスナーを設定
        Button clearBtn = (Button) findViewById(R.id.setDateBtn);
        clearBtn.setOnClickListener(new SetButtonClickListener());

        // 残り日数を表示するTextView
        dispView = (TextView) findViewById(R.id.dispView);
        // 年齢を表示するTextView
        ageView = (TextView) findViewById(R.id.ageView);

        // プレファレンスデータから呼び込む
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Activity.MODE_PRIVATE);
        year = prefs.getInt(YEAR, 0);
        month = prefs.getInt(MONTH, 0);
        day = prefs.getInt(DAY, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (year != 0) {
            // 読み込んだ値を表示
            showResult();
        } else {
            // メッセージを表示
            dispView.setText(DEFAULT_MSG);
        }
    }
}
