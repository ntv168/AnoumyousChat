package com.example.sam.anoumyouschat;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.anoumyouschat.adapter.MessageListAdapter;
import com.example.sam.anoumyouschat.message.Message;
import com.example.sam.anoumyouschat.photo.SelectImageActivity;
import com.example.sam.anoumyouschat.pojo.ResponseData;
import com.example.sam.anoumyouschat.pojo.User;
import com.example.sam.anoumyouschat.retrofit.APIInterface;
import com.example.sam.anoumyouschat.retrofit.RetrofitSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.apache.http.util.TextUtils;

public class ChatMain extends Activity {
    private String userid = "";
    private String name = "";
    private String conservasion = "conversation@BkUxdxPH-@Sy1zOgwHW";
    private EditText mInputMessageView;
    private List<Message> lstMsg = new ArrayList<>();
    private ListView lvMsg ;
    MessageListAdapter adapter;
    RetrofitSingleton retrofitSingleton;
    APIInterface apiInterface;
    Bitmap mBitmap;

    private static final int REQUEST_SELECT_IMAGE = 0;

    // LogCat tag
    private static final String TAG = ChatMain.class.getSimpleName();
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
//        Log.d(TAG, "call: -----------------------



        Bundle extra = getIntent().getBundleExtra("user");
        conservasion = extra.getString("conversation");
        userid = extra.getString("id");
        name = extra.getString("name");
        lvMsg = (ListView) findViewById(R.id.list_view_messages);
        mInputMessageView = (EditText) findViewById(R.id.txtMsg);


        ChatApplication app = (ChatApplication) this.getApplication();
        app.setConversationId(conservasion);
        mSocket = app.getSocket();

        mSocket.on("UPDATE_" + conservasion, onNewMessage);

        ((ImageView) findViewById(R.id.sendchat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend(mInputMessageView.getText().toString());
//                Toast.makeText(ChatMain.this, mInputMessageView.getText(), Toast.LENGTH_SHORT).show();
                Message msg = new Message();
                msg.setFromName(name);
                msg.setSelf(true);
                msg.setType("msg");
                msg.setMessage(mInputMessageView.getText() + "");
                mInputMessageView.setText("");
                lstMsg.add(msg);
                MessageListAdapter adapter = new MessageListAdapter(ChatMain.this, lstMsg);
                lvMsg.setAdapter(adapter);

            }
        });

        ((ImageView) findViewById(R.id.btnGetphoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ChatMain.this, SelectImageActivity.class),REQUEST_SELECT_IMAGE);
            }
        });

        mSocket.connect();
        adapter = new MessageListAdapter(ChatMain.this, lstMsg);
        lvMsg.setAdapter(adapter);

        lvMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lstMsg.get(position).getType().equals("image")) {
                    String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), lstMsg.get(position).getBitmap(),"title", null);
                    Uri bmpUri = Uri.parse(pathofBmp);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(bmpUri,"image/*");
                    startActivity(intent);
                    Toast.makeText(ChatMain.this, "Image ------------------", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void attemptSend(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }


        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "msg");
            obj.put("id", userid);
            obj.put("content", message);
            obj.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("SEND_"+conservasion, obj);

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatMain.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject obj = (JSONObject) args[0];
                    Message msg = new Message();
                    try {
//                            if (!obj.getString("id").equals(userid)){
                        msg.setMessage(obj.getString("content"));
                        msg.setSelf(false);
                        msg.setFromName(obj.getString("name"));
                        msg.setType("");
                        if (obj.getString("type").equals("image")) {
                            msg.setMessage("");

                            byte[] decodedString = Base64.decode(obj.getString("content"), Base64.DEFAULT);
                            Log.d(TAG, "run: bye-------------------" + decodedString);
                            mBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Log.d(TAG, "run: bitmap -------------------" + mBitmap);
                            msg.setType("image");
                            msg.setBitmap(mBitmap);
                        }
                        lstMsg.add(msg);
                        MessageListAdapter adapter = new MessageListAdapter(ChatMain.this, lstMsg);
                        lvMsg.setAdapter(adapter);
//                            }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "foo: " + args[0].toString());
                    MessageListAdapter adapter = new MessageListAdapter(ChatMain.this, lstMsg);
                    lvMsg.setAdapter(adapter);
                }
            });

        }


    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.leave:
                leave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onBackPressed() {
        leave();
    }

    public void leave() {
        Toast.makeText(this, "OUT", Toast.LENGTH_SHORT).show();
        apiInterface = retrofitSingleton.getInstance().getApiInterface();
        attemptSend("leave");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("-------------", "onActivityResult: " + requestCode);
        switch (requestCode)
        {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uriImagePicked = data.getData();

                    try {
                        InputStream image_stream = getContentResolver().openInputStream(uriImagePicked);
                        mBitmap = scaleDown(BitmapFactory.decodeStream(image_stream),0.5f,true);

                        Message msg = new Message();
                        msg.setFromName(name);
                        msg.setSelf(true);
                        msg.setMessage("");
                        msg.setType("image");
                        msg.setBitmap(mBitmap);
                        lstMsg.add(msg);
                        MessageListAdapter adapter = new MessageListAdapter(ChatMain.this, lstMsg);
                        lvMsg.setAdapter(adapter);



                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bs); //bm is the bitmap object
                        byte[] picture = bs.toByteArray();
                        String encoded = Base64.encodeToString(picture, Base64.NO_WRAP);

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("type", "image");
                            obj.put("id", userid);
                            obj.put("content", encoded);
                            obj.put("name", name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSocket.emit("SEND_"+conservasion, obj);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, 200,
                200, filter);
        return newBitmap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off("UPDATE_" + conservasion, onNewMessage);
    }


}

