import { useState } from 'react';
import { toast } from 'react-toastify';

export function useFormSubmit(submitFn, options = {}) {
  const [response, setResponse] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState({});

  const submit = (data) => {
    setLoading(true);

    return submitFn(data)
      .then((res) => {
        setResponse(res);
        setLoading(false);
        if (options.successMessage) {
          if (options.successToastType === 'info') {
            toast.info(options.successMessage);
          } else {
            toast.success(options.successMessage);
          }
        }
        if (options.onSuccess) {
          options.onSuccess(res);
        }
        return res;
      })
      .catch((err) => {
        console.log(err);
        setError(err);
        setLoading(false);
        toast.error(options.errorMessage || err.message);
        if (options.onError) {
          options.onError(err);
        }
        throw err;
      });
  };

  return { response, loading, error, submit };
}
