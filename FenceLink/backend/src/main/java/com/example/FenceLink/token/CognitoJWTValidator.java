package com.example.FenceLink.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.web.client.RestTemplate;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CognitoJWTValidator {

    private static final Logger logger = Logger.getLogger(CognitoJWTValidator.class.getName());
    private static final String COGNITO_JWKS_URL = "https://cognito-idp.ap-southeast-1.amazonaws.com/ap-southeast-1_akO3biVkp/.well-known/jwks.json";
    private static final String EXPECTED_ISSUER = "https://cognito-idp.ap-southeast-1.amazonaws.com/ap-southeast-1_akO3biVkp";
    private static final String EXPECTED_AUDIENCE = "5uo259ncntke03gq3s27cei7k8"; // Cognito App Client ID

    public static String validateToken(String token) {
        try {
            // Decode token to get key ID (kid)
            DecodedJWT decodedJWT = JWT.decode(token);
            String kid = decodedJWT.getKeyId();
            logger.info("Decoded Key ID: " + kid);

            // Fetch the public key from Cognito
            RSAPublicKey publicKey = getCognitoPublicKey(kid);
            if (publicKey == null) {
                String errorMessage = "Public key not found for key ID: " + kid;
                logger.warning(errorMessage);
                return errorMessage;
            }

            // Verify JWT
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(EXPECTED_ISSUER)
                    .withAudience(EXPECTED_AUDIENCE)
                    .build();

            verifier.verify(token);

            // Check additional claims
            if (!EXPECTED_ISSUER.equals(decodedJWT.getIssuer())) {
                String errorMessage = "Issuer mismatch. Expected: " + EXPECTED_ISSUER + ", but got: " + decodedJWT.getIssuer();
                logger.warning(errorMessage);
                return errorMessage;
            }

            if (!decodedJWT.getAudience().contains(EXPECTED_AUDIENCE)) {
                String errorMessage = "Audience mismatch. Expected: " + EXPECTED_AUDIENCE + ", but got: " + decodedJWT.getAudience();
                logger.warning(errorMessage);
                return errorMessage;
            }

            logger.info("Token successfully verified.");
            return "Token is valid";

        } catch (Exception e) {
            String errorMessage = "Token validation failed: " + e.getMessage();
            logger.warning(errorMessage);
            return errorMessage;
        }
    }

    private static RSAPublicKey getCognitoPublicKey(String kid) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            JWKS jwks = restTemplate.getForObject(COGNITO_JWKS_URL, JWKS.class);
            if (jwks != null) {
                for (JWK jwk : jwks.getKeys()) {
                    if (jwk.getKid().equals(kid)) {
                        return (RSAPublicKey) jwk.toPublicKey();
                    }
                }
            }
            logger.warning("No matching key ID found in JWKS for kid: " + kid);
            return null;
        } catch (Exception e) {
            logger.warning("Failed to fetch JWKS: " + e.getMessage());
            return null;
        }
    }

    public static boolean isAdmin(String token) {
        try {
            logger.info("Received token: " + token);

            // Validate token before checking claims
            String validationResult = validateToken(token);
            if (!"Token is valid".equals(validationResult)) {
                logger.warning("Token validation failed: " + validationResult);
                return false;
            }

            // Decode the token and check 'cognito:groups' for 'Admin' role
            DecodedJWT decodedJWT = JWT.decode(token);
            List<String> groups = decodedJWT.getClaim("cognito:groups").asList(String.class);
            logger.info("Extracted groups claim: " + groups);

            return groups != null && groups.contains("admin");
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to decode token: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.warning("Failed to check admin role: " + e.getMessage());
            return false;
        }
    }
}
