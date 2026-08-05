/**
 * Global Karate configuration loaded once per runner / environment.
 * Values here are available as variables in every feature (e.g. baseUrl).
 */
function fn() {
  var env = karate.env; // null unless -Dkarate.env=...
  karate.log('karate.env system property was:', env);

  if (!env) {
    env = 'dev';
  }

  var config = {
    env: env,
    baseUrl: 'https://api.demoblaze.com',
    // Shared default password for generated users (not a real secret)
    defaultPassword: 'SecurePass123!',
    // Expected API error messages (exact strings returned by DemoBlaze)
    messages: {
      userAlreadyExists: 'This user already exist.',
      wrongPassword: 'Wrong password.',
      userDoesNotExist: 'User does not exist.'
    }
  };

  // Optional environment overrides
  if (env === 'dev') {
    config.baseUrl = 'https://api.demoblaze.com';
  }

  // Reasonable timeouts for a public demo API
  karate.configure('connectTimeout', 10000);
  karate.configure('readTimeout', 15000);
  // Pretty HTTP logs in console / HTML report
  karate.configure('logPrettyRequest', true);
  karate.configure('logPrettyResponse', true);

  return config;
}
