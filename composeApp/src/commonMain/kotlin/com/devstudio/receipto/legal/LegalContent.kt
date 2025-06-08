package com.devstudio.receipto.legal

object LegalContent {

    const val FAQS_CONTENT = """
    # Frequently Asked Questions (FAQs)

    ## General
    **Q: What is ReceiptO?**
    A: ReceiptO helps you manage and track your receipts digitally.

    **Q: Is ReceiptO free to use?**
    A: Yes, ReceiptO is currently free.

    ## Account
    **Q: How do I create an account?**
    A: You can sign in using your Google account.

    **Q: How do I delete my account?**
    A: You can delete your account from the Settings > Account section. This will remove your data.

    ## Features
    **Q: How do I add a receipt?**
    A: Navigate to the main screen and tap the 'Add' button.

    *(More placeholder questions can be added here)*

    ---
    *Please replace this with your full FAQs on your Notion page.*
    *Placeholder URL: [YOUR_NOTION_FAQS_URL_HERE]*
    """

    const val TERMS_OF_SERVICE_CONTENT = """
    # Terms of Service for ReceiptO

    **Last Updated:** [Date]

    Welcome to ReceiptO! By using our app, you agree to these terms.

    1.  **Acceptance of Terms:** Your access to and use of ReceiptO is conditioned on your acceptance of and compliance with these Terms.
    2.  **User Accounts:** You are responsible for safeguarding your account. We use Google Sign-In for authentication.
    3.  **Use of Service:** You agree not to misuse the service or help anyone else to do so.
    4.  **Receipt Data:** You own the receipt data you input. We are not responsible for any loss of data.
    5.  **Intellectual Property:** The Service and its original content, features, and functionality are owned by [Your Company/Name] and are protected by international copyright.
    6.  **Termination:** We may terminate or suspend your account if you breach these Terms.
    7.  **Disclaimers:** The service is provided "AS IS."
    8.  **Limitation of Liability:** In no event shall [Your Company/Name] be liable for any indirect damages.
    9.  **Governing Law:** These Terms shall be governed by the laws of [Your Jurisdiction].
    10. **Changes to Terms:** We reserve the right to modify these terms at any time.
    11. **Contact Us:** If you have any questions, please contact us at kavinalmighty@gmail.com.

    ---
    *Please replace this with your full Terms of Service on your Notion page.*
    *Placeholder URL: [YOUR_NOTION_TOS_URL_HERE]*
    """

    const val PRIVACY_POLICY_CONTENT = """
    # Privacy Policy for ReceiptO

    **Last Updated:** [Date]

    This policy describes how ReceiptO collects, uses, and shares your information.

    1.  **Information We Collect:**
        *   **Account Information:** When you sign in with Google, we collect your email address and name as provided by Google.
        *   **Receipt Data:** We collect and store the receipt information you enter, including names, amounts, dates, reasons, and any images you upload. Images are stored in Firebase Storage. Other receipt data is stored in Firebase Firestore/Realtime Database.
    2.  **How We Use Your Information:**
        *   To provide and maintain the Service.
        *   To manage your account.
        *   To improve the Service.
    3.  **How We Share Your Information:**
        *   We use Firebase services (Authentication, Firestore, Storage) provided by Google. Your data is subject to Google's privacy policies in relation to these services.
        *   We do not sell your personal information to third parties.
    4.  **Data Storage and Security:** We take reasonable measures to protect your information, but no system is 100% secure. Data is stored on Firebase servers.
    5.  **User Rights:** You can access your data and request account deletion via the app's settings. Deleting your account will remove your personal information and receipt data from our active databases.
    6.  **Children's Privacy:** Our service is not intended for children under 13.
    7.  **Changes to This Policy:** We may update this policy. We will notify you of any changes by posting the new policy in the app.
    8.  **Contact Us:** For any questions, contact kavinalmighty@gmail.com.

    ---
    *Please replace this with your full Privacy Policy on your Notion page.*
    *Placeholder URL: [YOUR_NOTION_PRIVACY_URL_HERE]*
    """
}
