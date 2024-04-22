package com.example.grid_image_view.errorhandling;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * original in http://bytes.babbel.com/en/articles/2016-03-16-retrofit2-rxjava-error-handling.html
 */
public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
  private final RxJava2CallAdapterFactory original;
  private final IAPIExceptionMapper errorDecode;

  private RxErrorHandlingCallAdapterFactory(IAPIExceptionMapper errorDecode) {
    this.errorDecode = errorDecode;
    original = RxJava2CallAdapterFactory.create();
  }

  public static CallAdapter.Factory create(IAPIExceptionMapper errorDecode) {
    return new RxErrorHandlingCallAdapterFactory(errorDecode);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public CallAdapter get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit), errorDecode);
  }

  private static class RxCallAdapterWrapper implements CallAdapter {
    private final Retrofit retrofit;
    private final CallAdapter wrapped;
    IAPIExceptionMapper errorDecode;
    private Call call;

    public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter wrapped, IAPIExceptionMapper errorDecode) {
      this.retrofit = retrofit;
      this.wrapped = wrapped;
      this.errorDecode = errorDecode;
    }

    @NotNull
    @Override
    public Type responseType() {
      return wrapped.responseType();
    }

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public Object adapt(Call call) {
      this.call = call;
      Object adaptedCall = wrapped.adapt(call);

      if (adaptedCall instanceof Completable) {

        return ((Completable) adaptedCall).onErrorResumeNext(throwable ->
        {
          try {
            String request = new Gson().toJson((this.call).request());
            String url = new Gson().toJson((this.call).request().url().url());
            return Completable.error(asRetrofitException(url, request, throwable));
          } catch (Exception e) {
            return Completable.error(asRetrofitException("", "", throwable));
          }
        });
      }

      if (adaptedCall instanceof Single) {
        return ((Single) adaptedCall).onErrorResumeNext(
          (Function<Throwable, SingleSource>) throwable -> {
            try {
              String request = new Gson().toJson((this.call).request());
              String url = new Gson().toJson((this.call).request().url().url());
              return Single.error(asRetrofitException(url, request, throwable));
            } catch (Exception e) {
              return Single.error(asRetrofitException("", "", throwable));
            }
          }
        );
      }

      if (adaptedCall instanceof Observable) {
        return ((Observable) adaptedCall).onErrorResumeNext((Function<Throwable, ObservableSource>) throwable -> {
          try {
            String request = new Gson().toJson((this.call).request());
            String url = new Gson().toJson((this.call).request().url().url());
            return Observable.error(asRetrofitException(url, request, throwable));
          } catch (Exception e) {
            return Observable.error(asRetrofitException("", "", throwable));
          }
        });
      }
      throw new RuntimeException("Observable Type not supported");
    }

    private Throwable asRetrofitException(String apiUrl, String request, Throwable throwable) {
      // We had non-200 http error
      if (throwable instanceof HttpException) {
        HttpException httpException = (HttpException) throwable;
        return errorDecode.decodeHttpException(apiUrl, request, httpException);
      }
      // A network error happened
      if (throwable instanceof IOException) {
        return errorDecode.decodeIOException(apiUrl, request, (IOException) throwable);
      }

      // We don't know what happened. We need to simply convert to an unknown error
      return errorDecode.decodeUnexpectedException(apiUrl, request, throwable);
    }
  }
}