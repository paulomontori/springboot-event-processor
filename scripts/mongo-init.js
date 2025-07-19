use purchasesdb;

db.createCollection('purchases');
db.purchases.createIndex({ purchaseId: 1 }, { unique: true });
