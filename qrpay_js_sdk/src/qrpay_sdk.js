import Context from './context';
import QRPAY_STORAGE from './qrpay_storage';

const QRPAY_SDK = () => {
  const context = Context();
  const qrpay_storage = QRPAY_STORAGE();
  const { qrpayBaseUrl, loggable } = context;

  const createUrl = (path) => `${qrpayBaseUrl}${path}`;

  if (loggable) {
    console.log('qrpaySdk Context[' + '|' + context + ']');
  }

  const authenticate = async (username, password) => {
    console.log(username, password);
    const data = await fetchDataAsync(createUrl(AUTH_APIS.AUTH_LOGIN), { loginId: username, password: password });

    if (loggable) {
      console.log('Authentication data:', data);
    }

    if (data.ok) {
      const { accessToken, accessTokenExpiresIn, refreshToken } = data;
      qrpay_storage.save('accessToken', accessToken);
      qrpay_storage.save('accessTokenExpiresIn', accessTokenExpiresIn);
      qrpay_storage.save('refreshToken', refreshToken);
    }

    return data;
  };

  const refresh = async () => {
    const refreshToken = qrpay_storage.find('refreshToken');
    if (!refreshToken) {
      return { ok: false, error: 'No refresh token available' };
    }

    const data = await fetchDataAsync(createUrl(AUTH_APIS.AUTH_REFRESH), { refreshToken: refreshToken });

    if (loggable) {
      console.log('Refresh data:', data);
    }

    if (data.ok) {
      const { accessToken, accessTokenExpiresIn } = data;
      qrpay_storage.save('accessToken', accessToken);
      qrpay_storage.save('accessTokenExpiresIn', accessTokenExpiresIn);
      return data;
    }
  };

  const logout = async () => {
    const refreshToken = qrpay_storage.find('refreshToken');
    if (!refreshToken) {
      return { ok: false, error: 'No refresh token available' };
    }

    const data = await fetchDataAsync(createUrl(AUTH_APIS.AUTH_LOGOUT), { refreshToken: refreshToken });
    if (!data.ok) {
      console.error('Logout failed:', data);
    }
    qrpay_storage.remove('accessToken');
    qrpay_storage.remove('accessTokenExpiresIn', accessTokenExpiresIn);
    qrpay_storage.remove('refreshToken');
    return true;
  };

  const getAccessToken = () => {
    return {
      accessToken: qrpay_storage.find('accessToken'),
      accessTokenExpiresIn: qrpay_storage.find('accessTokenExpiresIn'),
    };
  };

  const getRefreshToken = () => {
    return qrpay_storage.find('refreshToken');
  };

  const verifyAccessToken = () => {
    const { accessToken, accessTokenExpiresIn } = getAccessToken();
    if (!accessToken) {
      return false;
    }
    if (Date.now() >= accessTokenExpiresIn) {
      return false;
    }
    return true;
  };

  const apiAsyncRequest = async (apiPath, requestData) => {
    if (loggable) {
      console.log('API Request to:', apiPath, 'with data:', requestData);
    }

    if (!verifyAccessToken()) {
      const refreshResult = await refresh();
      if (!refreshResult.ok) {
        return QRPAY_CODE.RE_ATHENTICATE;
      }
    }
    const { accessToken } = getAccessToken();
    const url = createUrl(apiPath);
    return await fetchDataAsync(url, requestData, accessToken);
  };

  async function fetchDataAsync(url, data, accessToken) {
    const authHeader = accessToken ? { Authorization: `Bearer ${accessToken}` } : {};

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...authHeader,
        },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const data = await response.json();
        return { ok: true, ...data };
      }

      const errorBody = await response.json().catch(() => ({}));
      console.log('Http status:', response.status, response.statusText);
      console.log('Error body:', errorBody);
      return { ok: false, status: response.status, statusText: response.statusText, error: errorBody };
    } catch (error) {
      //Promise 자체가 rejected (network error, CORS 등)
      console.error('Fetch error:', error);
      return { ...QRPAY_CODE.FETCH_ERROR, error: error };
    }
  }

  function fetchDataPromise(url, data, accessToken) {
    const authHeader = accessToken ? { Authorization: `Bearer ${accessToken}` } : {};

    try {
      const response = fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...authHeader,
        },
        body: JSON.stringify(data),
      });
      return response;
    } catch (error) {
      console.error('Fetch error:', error);
      return new Promise((resolve, reject) => {
        reject({ ...QRPAY_CODE.FETCH_ERROR, error: error });
      });
    }
  }

  const publicAPI = {
    getAccessToken: getAccessToken,
    getRefreshToken: getRefreshToken,
    verifyAccessToken: verifyAccessToken,
    authenticate: authenticate,
    refresh: refresh,
    logout: logout,
    apiAsyncRequest: apiAsyncRequest,
    fetchDataPromise: fetchDataPromise,
    QRPAY_CODE: QRPAY_CODE,
    AUTH_APIS: AUTH_APIS,
  };

  return publicAPI;
};

const AUTH_APIS = {
  AUTH_LOGIN: '/auth/login',
  AUTH_REFRESH: '/auth/refresh',
  AUTH_LOGOUT: '/auth/logout',
};

const QRPAY_CODE = {
  RE_ATHENTICATE: {
    ok: false,
    code: 'EQ401',
    message: 'Authentication required.',
  },
  FETCH_ERROR: {
    ok: false,
    code: 'EQ999',
    message: 'Fetch Promise Rejected(Network error, CORS, etc.)',
  },
  API_ERROR: {
    ok: false,
    code: 'EQ500',
    message: 'application error',
  },
};

export default QRPAY_SDK;
