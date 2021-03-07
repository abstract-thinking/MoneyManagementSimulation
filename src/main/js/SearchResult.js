import React from "react";

const SearchResult = ({ result }) => (
  <div>
    {result.id}
    <br />
    {result.wkn}
    <br />
    {result.name}
    <br />
    {result.quantity}
    <br />
    {result.purchasePrice}
    <br />
    {result.transactionCosts}
    <br />
    {result.investment}
    <br />
    {result.notionalSalesPrice}
    <br />
    {result.notionalRevenue}
    <br />
    {result.positionRisk}
    <br />
  </div>
);

export default SearchResult;
