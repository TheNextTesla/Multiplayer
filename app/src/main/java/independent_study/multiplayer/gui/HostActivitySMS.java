package independent_study.multiplayer.gui;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import independent_study.multiplayer.R;
import independent_study.multiplayer.sms.BroadcastReceiverSMS;
import independent_study.multiplayer.util.DispatchActivity;

public class HostActivitySMS extends DispatchActivity
{
    private ListView listView;
    private EditText editText;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_sms);

        listView = findViewById(R.id.listViewHostSMS);
        editText = findViewById(R.id.editTextHostSMS);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, android.R.id.text1);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                displayUpdatedList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                try
                {
                    String selectedNumber = arrayAdapter.getItem(i);
                    String[] tildaSplit = selectedNumber.split("~");
                    StringBuilder numberBuilder = new StringBuilder();

                    for (int j = 0; j < tildaSplit[0].length(); j++)
                    {
                        char tempChar = tildaSplit[0].charAt(j);
                        if(Character.isDigit(tempChar))
                            numberBuilder.append(tempChar);
                    }

                    editText.setText(numberBuilder.toString());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        listView.setAdapter(arrayAdapter);
        displayUpdatedList(null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        displayUpdatedList(editText.getText().toString());
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void displayUpdatedList(String numberSoFar)
    {
        ArrayList<String> contactHeaders = new ArrayList<>();
        ArrayList<String> contactNumbers = new ArrayList<>();

        try
        {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    try
                    {
                        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                        {
                            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            while(phoneCursor.moveToNext())
                            {
                                int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String phone = phoneCursor.getString(phoneIndex);
                                contactHeaders.add(name);
                                contactNumbers.add(phone);
                            }

                            phoneCursor.close();
                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }

            cursor.close();
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

        ArrayList<String> listToDisplay = new ArrayList<>();
        boolean isPhoneEntryReal = numberSoFar != null && !numberSoFar.equals("");

        for(int i = contactNumbers.size() - 1; i >= 0; i--)
        {
            String contactNumber = contactNumbers.get(i);
            String contactHeader = contactHeaders.get(i);

            if(!isPhoneEntryReal || phoneNumberContains(contactNumber, numberSoFar))
            {
                String newString = String.format(Locale.US, "%s ~(%s)", contactNumber, contactHeader);
                listToDisplay.add(newString);
            }
        }

        contactHeaders.clear();
        contactNumbers.clear();

        arrayAdapter.clear();
        arrayAdapter.addAll(listToDisplay);
    }

    private boolean phoneNumberContains(String contactNumber, String numberSoFar)
    {
        if(contactNumber.contains(numberSoFar))
            return true;

        StringBuilder spaceBuilder = new StringBuilder();
        StringBuilder parenBuilder = new StringBuilder();
        parenBuilder.append("(");

        for(int i = 0; i < numberSoFar.length(); i++)
        {
            if(i % 3 == 0 && i != 0)
            {
                if(i < 9)
                    spaceBuilder.append(" ");

                if(i == 3)
                    parenBuilder.append(") ");
                else if(i == 6)
                    parenBuilder.append("-");
            }

            spaceBuilder.append(numberSoFar.charAt(i));
            parenBuilder.append(numberSoFar.charAt(i));
        }

        return contactNumber.contains(spaceBuilder.toString()) || contactNumber.contains(parenBuilder.toString());
    }
}
