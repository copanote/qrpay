const Context = () => {
  const profile = `${PROFILE}`;
  const context = (p) => {
    switch (p) {
      case 'local':
        return {
          qrpayBaseUrl: 'http://127.0.0.1:9090',
          loggable: true,
        };
      case 'development':
        return {
          qrpayBaseUrl: 'http://127.0.0.1:9090',
          loggable: true,
        };
      case 'production':
        return {
          qrpayBaseUrl: '',
          loggable: false,
        };
    }
  };

  return context(profile);
};

export default Context;
