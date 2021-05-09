import React from "react";

const OneViewEntry = ({ entry }) => (
  <tr className="entry-data">
    <td className="text-content">{entry.wkn}</td>
    <td className="text-content">{entry.name}</td>
    <td className="number-content">{entry.rsl.toFixed(2)}</td>
    <td className="number-content">{entry.purchasePrice.toFixed(2)}</td>
    <td className="number-content">{entry.currentPrice.toFixed(2)}</td>
    <td className="number-content">{entry.currentStopPrice.toFixed(2)}</td>
    <td className="number-content">{entry.initialStopPrice.toFixed(2)}</td>
  </tr>
);

export default OneViewEntry;
