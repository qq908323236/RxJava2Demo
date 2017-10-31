package com.fu.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Subscription mSubscription;

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button btnrequest;
    private Button button10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.button10 = (Button) findViewById(R.id.button10);
        this.btnrequest = (Button) findViewById(R.id.btn_request);
        this.button9 = (Button) findViewById(R.id.button9);
        this.button8 = (Button) findViewById(R.id.button8);
        this.button7 = (Button) findViewById(R.id.button7);
        this.button6 = (Button) findViewById(R.id.button6);
        this.button5 = (Button) findViewById(R.id.button5);
        this.button4 = (Button) findViewById(R.id.button4);
        this.button3 = (Button) findViewById(R.id.button3);
        this.button2 = (Button) findViewById(R.id.button2);
        this.button1 = (Button) findViewById(R.id.button1);
        initListener();

    }

    private void initListener() {
        this.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method1();
            }
        });
        this.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method2();
            }
        });

        this.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method3();
            }
        });
        this.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method4();
            }
        });
        this.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method5();
            }
        });
        this.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method6();
            }
        });
        this.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method7();
            }
        });
        this.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method8();
            }
        });
        this.button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method9();
            }
        });
        this.btnrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubscription.request(128);
            }
        });
        this.button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method10();
            }
        });
    }

    /**
     * 基本使用
     */
    private void method1() {
        //创建一个上游Observable(被观察者)
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            //ObservableEmitter发射器,可以发送三种类型的事件,
            //通过调用emitter的onNext(T v),onComplet(),onError()，就可以分别发送三种不同的事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //发送4个事件
                Log.d(TAG, "emit 1");
                emitter.onNext(1);

                Log.d(TAG, "emit 2");
                emitter.onNext(2);

                Log.d(TAG, "emit 3");
                emitter.onNext(3);

                Log.d(TAG, "emit complete");
                emitter.onComplete();
            }
        });
        //创建一个下游Observer(观察者)
        Observer<Integer> observer = new Observer<Integer>() {
            //Disposable是上游和下游中间的一个机关
            //当调用d.dispose()，会使下游收不到事件,但上游还是会继续发送事件的
            @Override
            public void onSubscribe(Disposable d) {
                //先执行这个回调，才开始发送事件
                Log.i(TAG, "onSubscribe: ");

            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };
        //建立连接,并且上游开始发送事件
        observable.subscribe(observer);
    }

    /**
     * 线程控制
     */
    private void method2() {
        //创建一个observable(被观察者)
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "上游的线程是: " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        //创建一个只实现了onNext的observer(观察者)
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "下游的线程是 :" + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };


        //建立连接
        /**
         * 多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
         *
         * 多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
         */
        observable.subscribeOn(Schedulers.newThread())  //指定上游(被观察者)发送事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定下游(观察者)接收事件的线程
                .subscribe(consumer);

    }

    /**
     * 与retrofit结合使用的案例，网络请求
     */
    private void method3() {
        //创建retrofit客户端
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.123/")   //必须以'/'结尾
                .addConverterFactory(GsonConverterFactory.create())    //以Gson来解析获取的结果
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //与RxJava2结合使用就要添加适配器
                .build();

        //用Retrofit创建一个Api接口的代理对象
        Api api = retrofit.create(Api.class);
        //接口调用
        api.getUser()
                .subscribeOn(Schedulers.io())       //在io线程中进行网络请求(上游)
                .observeOn(AndroidSchedulers.mainThread())    //回到主线程中处理请求结果
                .subscribe(new Observer<User>() {             //建立连接
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "onNext: ");
                        Log.i(TAG, "User:" + user.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * map变换操作符
     * 通过Map, 可以将上游发来的事件转换为任意的类型, 可以是一个Object, 也可以是一个集合
     */
    private void method4() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);

                Log.d(TAG, "emit 2");
                emitter.onNext(2);

                Log.d(TAG, "emit 3");
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                //在这里，我们把接收到上游来的整形变换成字符串类型
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                //这里接收到的就是String类型了
                Log.i(TAG, "accept: " + s);
            }
        });
    }

    /**
     * flatMap操作符
     * FlatMap将一个发送事件的上游Observable变换为多个发送事件的Observables，
     * 然后将它们发射的事件合并后放进一个单独的Observable里.
     * 这里需要注意的是, flatMap并不保证事件的顺序, 并不是事件1就在事件2的前面.
     * 如果需要保证顺序则需要使用concatMap,把里面的flatMap改成concatMap,就是有序的了
     * <p>
     * 这里的例子是：
     * 我们在flatMap中将上游发来的每个事件转换为一个新的发送三个String事件的水管,
     * 为了看到flatMap结果是无序的,所以加了10毫秒的延时
     * 把里面的flatMap改成concatMap,就是有序的了
     */
    private void method5() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);

                Log.d(TAG, "emit 2");
                emitter.onNext(2);

                Log.d(TAG, "emit 3");
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                //返回的类型还是被观察者（上游）,但泛型改变成了String
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                return Observable.fromIterable(list).delay(1000, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "accept: " + s);
            }
        });
    }

    /**
     * 线程控制+操作符+retrofit的案例
     * 一个新用户, 必须先注册, 等注册成功之后再自动登录的实现方法
     */
    private void method6() {
        //先创建retrofit客户端
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.123/")   //必须以'/'结尾
                .addConverterFactory(GsonConverterFactory.create())    //以Gson来解析获取的结果
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //与RxJava2结合使用就要添加适配器
                .build();

        final Api api = retrofit.create(Api.class);
        api.register()   //发起注册请求
                .subscribeOn(Schedulers.io())     //在io线程中执行网络请求
                .observeOn(AndroidSchedulers.mainThread())    //回到主线程中去处理注册的请求结果
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        //先根据注册的结果去处理一些UI操作之类的
                        Log.d(TAG, "accept: " + responseBody.string());
                    }
                })
                .observeOn(Schedulers.io())        //回到io线程去发起登录请求
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody responseBody) throws Exception {
                        Log.d(TAG, "apply: 请求登录");
                        return api.login();   //进行登录请求，返回的还是Observable(被观察者)
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())   //回到主线程中去处理登录的请求结果
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        //根据subscribe()的重载，传入的Consumer接收的事件类型不同
                        //这里是接受onNext的
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //根据subscribe()的重载，传入的Consumer接收的事件类型不同
                        //这里是接收onError的
                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * zip操作符
     * Zip通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件.
     * 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
     * 注意：要让几个observable在不同的线程中，才能一起发送
     */
    private void method7() {
        /**
         * 先创建了两个被观察者，一个int类型的一个是String类型的
         */
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);


                Log.d(TAG, "emit 2");
                emitter.onNext(2);


                Log.d(TAG, "emit 3");
                emitter.onNext(3);


                Log.d(TAG, "emit 4");
                emitter.onNext(4);


                Log.d(TAG, "emit complete1");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d(TAG, "emit A");
                emitter.onNext("A");


                Log.d(TAG, "emit B");
                emitter.onNext("B");


                Log.d(TAG, "emit C");
                emitter.onNext("C");


                Log.d(TAG, "emit complete2");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        //zip操作符,BiFunction<>中的泛型,
        // 1:第一个observable的泛型 2:第二个observable的泛型 3.我们组合成的observable的泛型
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    /**
     * zip实例
     * 比如一个界面需要展示用户的一些信息, 而这些信息分别要从两个服务器接口中获取,
     * 而只有当两个都获取到了之后才能进行展示, 这个时候就可以用Zip了:
     */
    private void method8() {
        //先创建retrofit客户端
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.123/")   //必须以'/'结尾
                .addConverterFactory(GsonConverterFactory.create())    //以Gson来解析获取的结果
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //与RxJava2结合使用就要添加适配器
                .build();
        Api api = retrofit.create(Api.class);

        Observable<ResponseBody> observable1 = api.getUserBaseInfo().subscribeOn(Schedulers.io());
        Observable<ResponseBody> observable2 = api.getUserExtraInfo().subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<ResponseBody, ResponseBody, String>() {
            @Override
            public String apply(ResponseBody responseBody, ResponseBody responseBody2) throws Exception {
                return responseBody.string() + " ; " + responseBody2.string();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept: " + s);
                    }
                });
    }


    /**
     * Flowable的基本使用
     */
    private void method9() {
        //上游
        Flowable<Integer> upstream = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emitter.requested : " + emitter.requested());

                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();

            }
        }, BackpressureStrategy.ERROR);//增加了一个参数,用来选择背压策略,也就是出现上下游流速不均衡的时候应该怎么处理的办法
        //BackpressureStrategy.ERROR:上下游流速不均衡的时候直接抛出一个异常,这个异常就是著名的MissingBackpressureException
        //BackpressureStrategy.BUFFER:水缸无限制
        //BackpressureStrategy.DROP:  把存不下的事件丢弃
        //BackpressureStrategy.LATEST: 只保留最新的事件

        //下游
        Subscriber<Integer> downstream = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                //s.cancel(); 调用这个也能切断水管
                Log.i(TAG, "onSubscribe: ");
//                s.request(Long.MAX_VALUE);   //设置下游的处理能力,不调用这个，下游收不到事件
                mSubscription = s;
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };

        upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(downstream);
    }

    /**
     * Flowable的背压策略
     * BackpressureStrategy.ERROR:上下游流速不均衡的时候直接抛出一个异常,这个异常就是著名的MissingBackpressureException
     * BackpressureStrategy.BUFFER:水缸无限制
     * BackpressureStrategy.DROP:  把存不下的事件丢弃
     * BackpressureStrategy.LATEST: 只保留最新的事件
     * <p>
     * s.request(n),设置处理能力，同步的时候才有用，异步的时候，内部自己设置为128的
     */
    private void method10() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 10000; i++) {  //只发1W个事件
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.i(TAG, "onSubscribe: ");
                        mSubscription = s;
                        s.request(128); //一开始就处理128个事件
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }
}
