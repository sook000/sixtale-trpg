import axios from 'axios';

export const ROLE_ASSISTANT = 'assistant';
export const ROLE_SYSTEM = 'system';
export const ROLE_USER = 'user';

export const FINISH_REASON_LENGTH = 'length';
export const FINISH_REASON_STOP = 'stop';

const createClient = (key) => axios.create({
  baseURL: 'https://api.chatpdf.com',
  headers: {
    'x-api-key': key,
  },
});

const createCompletion = (client) => async ({ messages }) => {
  try {
    const response = await client.post('/v1/chats/message', {
      sourceId: 'cha_M8NdiexLZevpUbUk55pgT',
      messages,
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

export {
  createClient,
  createCompletion,
};