const {
	MONGO_HOST,
	MONGO_PORT,
	MONGO_DB,
} = process.env;

module.exports = {
  db: `mongodb://${MONGO_HOST}:${MONGO_PORT}/${MONGO_DB}`
};
