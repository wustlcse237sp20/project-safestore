# Safe Store

## Completed User Stories:
1. From the UI, a user should be able to modify a billing address, card number, expiration date, CVV, PIN, and card nickname (default is last 4 digits) associated with a debit card so they can update their account with current information.
2. From the UI, a user should be able to add a billing address, card number, expiration date, CVV, PIN, and card nickname (default is last 4 digits) associated with a debit card so that they can view it later.
3. From the UI, a user should be able to view a billing address, card number, expiration date, CVV, PIN, and/or a card nickname (default is last 4 digits) associated with a debit card so they can use it to make online transactions.
4. A user should be able to clear their information (i.e. passwords, card numbers, etc) that is shown to them when they search account or card info.
5. A user should be able to log out of SafeStore.
6. A user should have their debit card information validated (i.e. debit card number is all numbers, etc.) so that they can't just store garbage information.
7. A user should have their credit card information validated (i.e. credit card number is all numbers, etc.) so that they can't just store garbage information.
8. A user should have to retype the password when creating an account to ensure they properly input their desired password.
9. A user should be able to retrieve a list of keys (nicknames) for their Website Accounts, Debit Cards, and Credit Cards so that if they forget what they nicknamed it they can find out.

## Implemented but Non-Working Stories:
- Everything we implemented is working!

## To see check our feature branch usage (not just using development and master):
- Go to all pull requests (inlcudes non-open PR): https://github.com/wustlcse237sp20/project-safestore/pulls?q=is%3Apr+
- You can click on any pull request and check at the top which branches are being merged
- Example: This pull request (https://github.com/wustlcse237sp20/project-safestore/pull/75) merges validateCreditInput into development
## Commands to Compile and Run Code:
Note: Do not copy and paste the $, they represent your terminal new lines

Note 2: You will not be able to run this from Eclipse, this is intentional.

Note 3: GUI does not seem to be compatible with Windows Subsystem for Linux. If this is what you're running, our project probably won't run/work.
```sh
$ git clone https://github.com/wustlcse237sp20/project-safestore.git
$ cd project-safestore/
$ ./runSafeStore.sh 
```
