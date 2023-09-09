const { Client } = require('procbridge')
const client = new Client('127.0.0.1', 25465)
client.request("teste", {"teste":"teste"})
