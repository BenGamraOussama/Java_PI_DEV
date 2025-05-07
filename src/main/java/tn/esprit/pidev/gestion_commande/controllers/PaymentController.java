package tn.esprit.pidev.gestion_commande.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;

public class PaymentController {
    @FXML
    private WebView stripeWebView;
    
    @FXML
    private Button cancelButton;
    
    private Stage stage;
    private double amount;
    private static final String STRIPE_PUBLIC_KEY = "pk_test_51QxpljD6AlTSOLn68hERQox7TsjDTtEQO9meE8oBaWuvxNhANwcsWoKPPL4OFTBsYhdyt2JzF2im40sYEG4kNc7M00vhOw1KPC";
    private static final String STRIPE_SECRET_KEY = "sk_test_51QxpljD6AlTSOLn68XoVtlTcNoxoGFSBetZfbWHDIKnnslx2dY2IwEtSMz3Qv2B04pFjShA03kaV8dJh1qJoRDfF00DL6U51aJ";
    
    @FXML
    public void initialize() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
        initializePaymentForm();
    }
    
    private void initializePaymentForm() {
        try {
            // Create a PaymentIntent
            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount((long)(amount * 100)) // convert to cents
                .setCurrency("eur")
                .build();
            
            PaymentIntent intent = PaymentIntent.create(createParams);
            
            // Load Stripe Elements in the WebView
            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <title>Paiement</title>
                    <script src="https://js.stripe.com/v3/"></script>
                    <style>
                        body { margin: 0; padding: 20px; font-family: Arial, sans-serif; }
                        .payment-form {
                            max-width: 500px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        #card-element {
                            margin: 20px 0;
                            padding: 12px;
                            border: 1px solid #e0e0e0;
                            border-radius: 4px;
                            background: white;
                        }
                        #submit-button {
                            background: #5469d4;
                            color: #ffffff;
                            font-family: Arial, sans-serif;
                            padding: 12px 16px;
                            border-radius: 4px;
                            border: 0;
                            font-size: 16px;
                            font-weight: 600;
                            cursor: pointer;
                            display: block;
                            box-shadow: 0 4px 5.5px 0 rgba(0, 0, 0, 0.07);
                            width: 100%%;
                            transition: all 0.2s ease;
                        }
                        #submit-button:hover {
                            filter: brightness(110%%);
                        }
                        #payment-message {
                            color: #697386;
                            font-size: 14px;
                            line-height: 20px;
                            padding-top: 12px;
                            text-align: center;
                        }
                        #payment-element {
                            margin-bottom: 24px;
                        }
                    </style>
                </head>
                <body>
                    <form id="payment-form" class="payment-form">
                        <div id="card-element"></div>
                        <button id="submit-button">
                            <span id="button-text">Payer %.2f €</span>
                        </button>
                        <div id="payment-message"></div>
                    </form>
                    <script>
                        var stripe = Stripe('%s');
                        var elements = stripe.elements();
                        var card = elements.create('card', {
                            style: {
                                base: {
                                    fontSize: '16px',
                                    color: '#32325d',
                                    fontFamily: 'Arial, sans-serif',
                                    '::placeholder': {
                                        color: '#aab7c4'
                                    }
                                },
                                invalid: {
                                    color: '#fa755a',
                                    iconColor: '#fa755a'
                                }
                            }
                        });
                        
                        card.mount('#card-element');
                        
                        var form = document.getElementById('payment-form');
                        var submitButton = document.getElementById('submit-button');
                        var messageDiv = document.getElementById('payment-message');
                        
                        form.addEventListener('submit', function(event) {
                            event.preventDefault();
                            submitButton.disabled = true;
                            messageDiv.textContent = 'Traitement en cours...';
                            
                            stripe.confirmCardPayment('%s', {
                                payment_method: {
                                    card: card,
                                }
                            }).then(function(result) {
                                submitButton.disabled = false;
                                if (result.error) {
                                    messageDiv.textContent = result.error.message;
                                    window.paymentStatus = 'error:' + result.error.message;
                                } else {
                                    messageDiv.textContent = 'Paiement réussi!';
                                    window.paymentStatus = 'success:' + result.paymentIntent.id;
                                }
                            });
                        });
                        
                        // Monitor payment status changes
                        var checkPaymentStatus = setInterval(function() {
                            if (window.paymentStatus) {
                                window.java.onPaymentStatus(window.paymentStatus);
                                clearInterval(checkPaymentStatus);
                            }
                        }, 100);
                    </script>
                </body>
                </html>
            """, amount, STRIPE_PUBLIC_KEY, intent.getClientSecret());
            
            stripeWebView.getEngine().loadContent(htmlContent);
            
            // Create a bridge between JavaScript and Java
            stripeWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    // Add JavaScript interface
                    JSObject window = (JSObject) stripeWebView.getEngine().executeScript("window");
                    window.setMember("java", new JavaScriptBridge());
                }
            });
            
        } catch (StripeException e) {
            handlePaymentError(e.getMessage());
        }
    }
    
    // JavaScript bridge class
    public class JavaScriptBridge {
        public void onPaymentStatus(String status) {
            javafx.application.Platform.runLater(() -> {
                if (status.startsWith("success:")) {
                    handlePaymentSuccess(status.substring(8));
                } else if (status.startsWith("error:")) {
                    handlePaymentError(status.substring(6));
                }
            });
        }
    }
    
    private void handlePaymentSuccess(String paymentIntentId) {
        showAlert(Alert.AlertType.INFORMATION, "Paiement réussi", 
                 "Votre paiement a été traité avec succès.\nID de transaction: " + paymentIntentId);
        stage.close();
    }
    
    private void handlePaymentError(String errorMessage) {
        showAlert(Alert.AlertType.ERROR, "Erreur de paiement", 
                 "Une erreur est survenue: " + errorMessage);
    }
    
    @FXML
    private void handleCancel() {
        stage.close();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
