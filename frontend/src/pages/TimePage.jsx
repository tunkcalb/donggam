import React from "react";
import TimeBackground from "../components/timepage/TimeBackground";
import CreateButton from "../components/common/CreateButton";

const Timepage = () => {
  return (
    <>
      <TimeBackground />
      <CreateButton to="/time/upload" />
    </>
  );
};

export default Timepage;