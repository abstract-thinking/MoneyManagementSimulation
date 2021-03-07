import React, { useState, useEffect } from "react";
import axios from "axios";
import PurchaseRecommendation from "./PurchaseRecommendation";
import Spinner from "react-bootstrap/Spinner";
import SearchResult from "./SearchResult.js";

const Search = ({ riskManagementId }) => {
  const [wkn, setWkn] = useState("");
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function requestWkn() {
    setLoading(true);
    setError("");

    axios
      .get("http://localhost:8080/api/search?wkn=" + wkn)
      .then(response => {
        setResult(response.data);
        console.log("Received data: ", response.data);
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  }

  if (isLoading) {
    return <Spinner animation="border" />;
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  return (
    <div className="search-params">
      <form
        onSubmit={e => {
          e.preventDefault();
          requestWkn();
        }}
      >
        <label htmlFor="wkn">
          WKN
          <input
            id="wkn"
            value={wkn}
            placeholder="WKN"
            onChange={e => setWkn(e.target.value)}
          />
        </label>
      </form>
      {result && <SearchResult result={result} />}
    </div>
  );
};

export default Search;
