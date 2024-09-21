import axios from 'axios';

const exile = axios.create({
  baseURL: 'http://i11d108.p.ssafy.io/api/v1:8888',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default exile;
