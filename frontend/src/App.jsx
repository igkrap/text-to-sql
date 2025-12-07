import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [query, setQuery] = useState('');
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [examples, setExamples] = useState([]);
  const [status, setStatus] = useState(null);

  useEffect(() => {
    fetchExamples();
    checkStatus();
  }, []);

  const fetchExamples = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/query/examples');
      setExamples(res.data);
    } catch (error) {
      console.error('예제 로드 실패:', error);
    }
  };

  const checkStatus = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/query/status');
      setStatus(res.data);
    } catch (error) {
      console.error('상태 확인 실패:', error);
      setStatus({
        status: 'offline',
        message: '백엔드 서버에 연결할 수 없습니다.'
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;

    setLoading(true);
    setResponse(null);

    try {
      const res = await axios.post('http://localhost:8080/api/query/execute', {
        naturalLanguageQuery: query
      });
      setResponse(res.data);
    } catch (error) {
      console.error('쿼리 실행 실패:', error);
      setResponse({
        error: '서버와의 통신에 실패했습니다. 백엔드 서버가 실행중인지 확인해주세요.'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleExampleClick = (exampleQuery) => {
    setQuery(exampleQuery);
  };

  const renderTable = (data) => {
    if (!data || data.length === 0) {
      return <p className="no-data">결과가 없습니다.</p>;
    }

    const columns = Object.keys(data[0]);

    return (
      <div className="table-container">
        <table className="result-table">
          <thead>
            <tr>
              {columns.map((col) => (
                <th key={col}>{col}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((row, idx) => (
              <tr key={idx}>
                {columns.map((col) => (
                  <td key={col}>{formatValue(row[col])}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const formatValue = (value) => {
    if (value === null || value === undefined) return '-';
    if (typeof value === 'object') return JSON.stringify(value);
    return String(value);
  };

  return (
    <div className="app">
      <div className="container">
        <header className="header">
          <h1>🏭 제조 NLP SQL 질의 시스템</h1>
          <p>자연어로 제조 데이터베이스를 질의하세요 (로컬 LLM Powered)</p>
          {status && (
            <div className={`status-badge ${status.status}`}>
              {status.ollama_available ? (
                <><span className="status-icon">🟢</span> Ollama 연결됨</>
              ) : (
                <><span className="status-icon">🔴</span> Ollama 연결 안됨</>
              )}
            </div>
          )}
        </header>

        <div className="main-content">
          <div className="query-section">
            <form onSubmit={handleSubmit}>
              <div className="input-group">
                <input
                  type="text"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  placeholder="예: 모든 제품 보여줘"
                  className="query-input"
                  disabled={loading}
                />
                <button type="submit" className="submit-btn" disabled={loading}>
                  {loading ? '실행중...' : '실행'}
                </button>
              </div>
            </form>

            <div className="examples">
              <h3>예제 쿼리:</h3>
              <div className="example-list">
                {examples.map((example, idx) => (
                  <button
                    key={idx}
                    onClick={() => handleExampleClick(example)}
                    className="example-btn"
                    disabled={loading}
                  >
                    {example}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {response && (
            <div className="response-section">
              {response.error ? (
                <div className="error-box">
                  <h3>❌ 오류</h3>
                  <p>{response.error}</p>
                </div>
              ) : (
                <>
                  <div className="sql-box">
                    <h3>생성된 SQL:</h3>
                    <pre>{response.generatedSql}</pre>
                    <div className="meta-info">
                      <span>실행시간: {response.executionTime}</span>
                      <span>결과 개수: {response.rowCount}건</span>
                    </div>
                  </div>

                  <div className="results-box">
                    <h3>쿼리 결과:</h3>
                    {renderTable(response.results)}
                  </div>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
