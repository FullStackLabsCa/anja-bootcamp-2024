const stripe = Stripe("pk_test_51QosuMRx3G56TkbcilINhYSTpUwf2pfRjL3hjaRK2Lx7HXf3ijdv6Ph47gdty12rLjhvhWQeUJsiyhVijopT1zNl00sUpqKP0O"); // Replace with your Stripe public key
    const elements = stripe.elements();
    const card = elements.create("card");
    card.mount("#card-element");

    document.querySelector("#submit").addEventListener("click", async () => {
        document.getElementById("submit").disabled = true;
        document.getElementById("message").innerText = "Processing payment...";

        const { paymentMethod, error } = await stripe.createPaymentMethod({
            type: "card",
            card: card
        });

        if (error) {
            console.error(error);
            document.getElementById("message").innerText = "Error: " + error.message;
            document.getElementById("submit").disabled = false;
        } else {
            console.log("PaymentMethod created:", paymentMethod);
            alert("Payment method id: " + paymentMethod.id);
            document.getElementById("message").innerText = "Payment successful";
            document.getElementById("submit").disabled = false;
        }
    });